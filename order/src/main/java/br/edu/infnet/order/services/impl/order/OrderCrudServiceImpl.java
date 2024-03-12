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
            Order novaOrderm = new Order();
            novaOrderm.setEmailUsuario(ordemRequest.getEmailUsuario());
            novaOrderm.setDataCriacao(LocalDateTime.now());
            novaOrderm.setItems(ordemRequest.getIdProdutos().stream().map(item -> new OrderItem(item, novaOrderm)).toList());
            novaOrderm.setUuid(UUID.randomUUID());
            novaOrderm.setTipoPagamento(ordemRequest.getTipoPagamento());
            novaOrderm.setStatus(this.solicitarPagamentoOrdem(novaOrderm));
            this.repository.saveAndFlush(novaOrderm);
            this.itemRepository.saveAll(novaOrderm.getItems());
            response = new OrderResponseDto(novaOrderm, this.recuperarInfoProdutos(ordemRequest.getIdProdutos()));
            this.enviarNotificacaoEmail(response, GeradorConteudoEmail.getGeradorFromOrderStatus(response.getOrderStatus()));
            return response;
        } catch (Exception e) {
            log.error("Falha ao criar nova orderm para o usuario {}", ordemRequest.getEmailUsuario(), e);
            this.enviarNotificacaoEmail(response, GeradorConteudoEmail.FALHA_CRIACAO_ORDEM);
            throw new RuntimeException("Falha ao criar nova ordem");
        }
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

            if(produtos.getBody() != null && produtos.getBody().getStatus() != null) {
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
                            throw new RuntimeException("Falha ao recuperar informacoes do produto");
                        }
                    }).block();
            if(produtos.getBody() != null && StringUtils.isNotBlank(produtos.getBody().getNome())) {
                return StringUtils.trimToEmpty(produtos.getBody().getNome());
            } else {
                throw new RuntimeException("Nao foi retornada informacao dos produtos");
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar informacoes dos produtos. Sera retornada lista vazia", e);
            return StringUtils.EMPTY;
        }
    }


    @Override
    public OrderResponseDto atualizarOrdem(OrderStatus novoStatus, String idOrdem) {
        try {
            final Order order = this.repository.findByUuid(UUID.fromString(idOrdem));
            if(order != null) {
                order.setStatus(novoStatus);
                this.repository.save(order);
                final List<Long> idProdutos = order.getItems().stream().map(OrderItem::getIdProduto).toList();
                return new OrderResponseDto(order, this.recuperarInfoProdutos(idProdutos));
            } else {
                throw new RuntimeException("Ordem %s nao encontrada".formatted(idOrdem));
            }
        } catch (Exception e) {
            log.error("Falha ao atualizar ordem {} para o status {}", idOrdem, novoStatus.name(), e);
        }
        return null;
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
            throw new RuntimeException("Nao foi possivel recuperar as ordens do usuario %s".formatted(emailUsuario));
        }
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
                            throw new RuntimeException("Falha ao recuperar informacoes do produto");
                        }
                    }).block();
            if(produtos != null) {
                return produtos;
            } else {
                throw new RuntimeException("Nao foi retornada informacao dos produtos");
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar informacoes dos produtos. Sera retornada lista vazia", e);
            return new ArrayList<>();
        }
    }

}