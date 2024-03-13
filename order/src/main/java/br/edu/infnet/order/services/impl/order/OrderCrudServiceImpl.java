package br.edu.infnet.order.services.impl.order;

import br.edu.infnet.order.domain.Order;
import br.edu.infnet.order.domain.OrderItem;
import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.dto.api.payment.PaymentRequestDto;
import br.edu.infnet.order.dto.api.payment.PaymentResponseDto;
import br.edu.infnet.order.dto.api.products.InfoProdutos;
import br.edu.infnet.order.dto.api.user.UsuarioResponse;
import br.edu.infnet.order.dto.queue.EmailNotificationQueueDto;
import br.edu.infnet.order.dto.request.OrderRequestDto;
import br.edu.infnet.order.dto.response.OrderItemRepository;
import br.edu.infnet.order.dto.response.OrderResponseDto;
import br.edu.infnet.order.exceptions.ApiServiceConnectionException;
import br.edu.infnet.order.exceptions.BusinessException;
import br.edu.infnet.order.exceptions.DatabaseConnectionException;
import br.edu.infnet.order.repository.OrderRepository;
import br.edu.infnet.order.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.order.services.interfaces.OrderGenericService;
import br.edu.infnet.order.utils.GeradorConteudoEmail;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderCrudServiceImpl extends GenericCrudServiceImpl<Order, Long, OrderRepository> implements OrderGenericService {
    private final AmqpTemplate rabbitTemplate;
    private final WebClient webClient;
    private final OrderItemRepository itemRepository;

    @Value("${rabbit.exchange.name}")
    private String exchange;

    @Value("${rabbit.routing.key}")
    private String routingKey;


    @Value("${product.api.path}")
    private String pathApiProduto;

    @Value("${product.api.port_number}")
    private Integer portNumberApiProduto;

    @Value("${product.api.host_name}")
    private String hostNameApiProduto;

    @Value("${payment.api.path}")
    private String paymentPath;

    @Value("${payment.api.port_number}")
    private Integer paymentPortNumber;

    @Value("${payment.api.host_name}")
    private String paymenthostName;

    @Value("${userinfo.api.path}")
    private String pathApiInfoUsuario;

    @Value("${userinfo.api.port_number}")
    private Integer portNumberApiInfoUsuario;

    @Value("${userinfo.api.host_name}")
    private String hostNameApiInfoUsuario;

    private static final String CURRENT_SCHEME = "http";

    private final ObjectMapper objectMapper;

    protected OrderCrudServiceImpl(OrderRepository repository, AmqpTemplate rabbitTemplate, WebClient webClient, OrderItemRepository itemRepository) {
        super(repository);
        this.rabbitTemplate = rabbitTemplate;
        this.webClient = webClient;
        this.itemRepository = itemRepository;
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public OrderResponseDto criarOrdem(OrderRequestDto ordemRequest) {
        OrderResponseDto response = null;
        try {
            Order novaOrderm = this.gerarNovaOrder(ordemRequest);
            this.repository.saveAndFlush(novaOrderm);
            this.itemRepository.saveAll(novaOrderm.getItems());
            response = new OrderResponseDto(novaOrderm, this.recuperarInfoProdutos(ordemRequest.getIdProdutos()));
            if(ordemRequest.isRequisicaoPagamento()) {
                this.enviarNotificacaoEmail(response, GeradorConteudoEmail.getGeradorFromOrderStatus(response.getOrderStatus()));
            }
            return response;
        } catch (BusinessException e) {
          throw e;
        } catch (Exception e) {
            log.error("Falha ao criar nova orderm para o usuario {}", ordemRequest.getEmailUsuario(), e);
            this.enviarNotificacaoEmail(response, GeradorConteudoEmail.FALHA_CRIACAO_ORDEM);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public OrderResponseDto atualizarOrdem(OrderStatus novoStatus, String idOrdem) {
        OrderResponseDto response = null;
        try {
            final Order order = this.repository.findByUuid(UUID.fromString(idOrdem));
            if(order != null) {
                order.setStatus(novoStatus);
                this.repository.save(order);
                final List<Long> idProdutos = order.getItems().stream().map(OrderItem::getIdProduto).toList();
                response = new OrderResponseDto(order, this.recuperarInfoProdutos(idProdutos));
                this.enviarNotificacaoEmail(response, GeradorConteudoEmail.getGeradorFromOrderStatus(novoStatus));
                return response;
            } else {
                throw new BusinessException("Ordem %s nao encontrada".formatted(idOrdem));
            }
        } catch (BusinessException e) {
          throw e;
        } catch (Exception e) {
            log.error("Falha ao atualizar ordem {} para o status {}", idOrdem, novoStatus.name(), e);
            this.enviarNotificacaoEmail(response, GeradorConteudoEmail.FALHA_ATUALIZACAO_ORDEM);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public OrderResponseDto removerProdutoOrdem(List<Long> idProdutoRemocao, String idOrdem) {
        OrderResponseDto response = null;
        try {
            final Order order = this.repository.findByUuid(UUID.fromString(idOrdem));
            if(order != null && order.getItems() != null) {
                if(OrderStatus.SHOPPING_CART.equals(order.getStatus())) {
                    List<OrderItem> novaItemList = order.getItems().stream().filter(item -> !idProdutoRemocao.contains(item.getIdProduto())).toList();
                    order.setItems(novaItemList);
                    this.repository.saveAndFlush(order);
                    this.itemRepository.saveAll(novaItemList);
                    response = new OrderResponseDto(order, this.recuperarInfoProdutos(novaItemList.stream().map(OrderItem::getIdProduto).toList()));
                    this.enviarNotificacaoEmail(response, GeradorConteudoEmail.ATUALIZACAO_ORDEM);
                    return response;
                } else {
                    throw new BusinessException("Ordem %s nao esta mais como carrinho de compras. Seus produtos nao podem ser alterados.".formatted(idOrdem));
                }
            } else {
                throw new BusinessException("Ordem %s nao encontrada".formatted(idOrdem));
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Falha ao remover produtos da ordem {}", idOrdem, e);
            this.enviarNotificacaoEmail(response, GeradorConteudoEmail.FALHA_ATUALIZACAO_ORDEM);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public List<OrderResponseDto> recuperarTodasOrdensUsuario(String emailUsuario) {
        try {
            final List<Order> ordersUsuario = this.repository.findAllByEmailUsuarioIgnoreCase(emailUsuario);
            if(ordersUsuario.isEmpty()) {
                log.debug("Nao foram encontradas ordens para o email {}", emailUsuario);
                return new ArrayList<>();
            } else {
                List<OrderResponseDto> response = new ArrayList<>();
                for(Order order : ordersUsuario) {
                    List<Long> idProdutos = order.getItems().stream().map(OrderItem::getIdProduto).toList();
                    response.add(new OrderResponseDto(order, this.recuperarInfoProdutos(idProdutos)));
                }
                return response;
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar ordens para o usuario {}", emailUsuario, e);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public Order findByUuid(String id) {
        return this.repository.findByUuid(UUID.fromString(id));
    }

    private OrderStatus solicitarPagamentoOrdem(Order novaOrderm) {
        try {
            final ResponseEntity<PaymentResponseDto> produtos = this.webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(CURRENT_SCHEME)
                            .host(this.paymenthostName)
                            .port(this.paymentPortNumber)
                            .path(this.paymentPath)
                            .build())
                    .bodyValue(new PaymentRequestDto(novaOrderm))
                    .exchangeToMono(response -> response.toEntity(PaymentResponseDto.class)
                    ).block();

            if(produtos != null && produtos.getBody() != null && produtos.getBody().getStatus() != null) {
                return OrderStatus.recoverStatusFromPaymentStatus(produtos.getBody().getStatus());
            } else {
                return OrderStatus.FAILED_PAYMENT;
            }
        } catch (Exception e) {
            log.error("Falha ao processar pagamento. Sera considerado como pagamento falho", e);
            return OrderStatus.FAILED_PAYMENT;
        }
    }

    private void enviarNotificacaoEmail(OrderResponseDto response, GeradorConteudoEmail geradorConteudo) {
        try {
            String nome = this.recuperarNomeUsuario(response.getEmailUsuario());
            this.rabbitTemplate.convertAndSend(
                    this.exchange,
                    this.routingKey,
                    this.objectMapper.writeValueAsString(new EmailNotificationQueueDto(response.getEmailUsuario(),
                            geradorConteudo.gerarConteudoEmail(response, nome),
                            geradorConteudo.gerarTituloEmail(response, nome)))
            );
        } catch (Exception e) {
            log.error("Falha ao enviar email de criacao de ordem para {} sobre ordem {}", response.getEmailUsuario(), response.getId(), e);
        }
    }

    private String recuperarNomeUsuario(String emailUsuario) {
        try {
            final ResponseEntity<UsuarioResponse> produtos = this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(CURRENT_SCHEME)
                            .host(this.hostNameApiInfoUsuario)
                            .port(this.portNumberApiInfoUsuario)
                            .path(this.pathApiInfoUsuario + "/email/%s".formatted(emailUsuario))
                            .build())
                    .exchangeToMono(response -> {
                        if (HttpStatus.OK.equals(response.statusCode())) {
                            return response.toEntity(UsuarioResponse.class);
                        } else {
                            log.error("Falha ao recuperar informacoes do usuario. Status: " + response.statusCode().value());
                            throw new ApiServiceConnectionException("AuthService", this.pathApiInfoUsuario);
                        }
                    }).block();
            if(produtos != null && produtos.getBody() != null && StringUtils.isNotBlank(produtos.getBody().getNome())) {
                return StringUtils.trimToEmpty(produtos.getBody().getNome());
            } else {
                log.error("Nao foi retornada informacoes do usuario.");
                return StringUtils.EMPTY;
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar informacoes dos produtos. Sera retornada lista vazia", e);
            return StringUtils.EMPTY;
        }
    }

    private Order gerarNovaOrder(OrderRequestDto ordemRequest) {
        Order novaOrderm = new Order();
        novaOrderm.setEmailUsuario(ordemRequest.getEmailUsuario());
        novaOrderm.setDataCriacao(LocalDateTime.now());
        novaOrderm.setItems(ordemRequest.getIdProdutos().stream().map(item -> new OrderItem(item, novaOrderm)).toList());
        novaOrderm.setUuid(UUID.randomUUID());
        novaOrderm.setTipoPagamento(ordemRequest.getTipoPagamento());
        if(ordemRequest.isRequisicaoPagamento()) {
            novaOrderm.setStatus(this.solicitarPagamentoOrdem(novaOrderm));
        } else {
            novaOrderm.setStatus(OrderStatus.SHOPPING_CART);
        }
        return novaOrderm;
    }

    private List<InfoProdutos> recuperarInfoProdutos(List<Long> idProdutos) {
        try {
            final List<InfoProdutos> produtos = this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(CURRENT_SCHEME)
                            .host(this.hostNameApiProduto)
                            .port(this.portNumberApiProduto)
                            .path(this.pathApiProduto)
                            .queryParam("produtos", idProdutos)
                            .build())
                    .exchangeToMono(response -> {
                        if (HttpStatus.OK.equals(response.statusCode())) {
                            return response.bodyToFlux(InfoProdutos.class).collectList();
                        } else {
                            throw new ApiServiceConnectionException("ProdutosService", this.pathApiProduto);
                        }
                    }).block();
            if(produtos != null) {
                return produtos;
            } else {
                log.error("Nao foi retornada informacao dos produtos informados: " + StringUtils.join(idProdutos, " , "));
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar informacoes dos produtos. Sera retornada lista vazia", e);
            return new ArrayList<>();
        }
    }

}