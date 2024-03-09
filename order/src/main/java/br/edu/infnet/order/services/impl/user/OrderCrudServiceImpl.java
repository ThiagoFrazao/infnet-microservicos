package br.edu.infnet.order.services.impl.user;

import br.edu.infnet.order.domain.Order;
import br.edu.infnet.order.domain.OrderItem;
import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.dto.api.InfoProdutos;
import br.edu.infnet.order.dto.request.OrderRequestDto;
import br.edu.infnet.order.dto.response.OrderItemRepository;
import br.edu.infnet.order.dto.response.OrderResponseDto;
import br.edu.infnet.order.repository.OrderRepository;
import br.edu.infnet.order.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.order.services.interfaces.OrderGenericService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        try {
            Order novaOrderm = new Order();
            novaOrderm.setStatus(ordemRequest.getStatus());
            novaOrderm.setEmailUsuario(ordemRequest.getEmailOrder());
            novaOrderm.setDataCriacao(LocalDateTime.now());
            novaOrderm.setItems(ordemRequest.getIdProdutos().stream().map(item -> new OrderItem(item, novaOrderm)).toList());
            this.repository.saveAndFlush(novaOrderm);
            this.itemRepository.saveAll(novaOrderm.getItems());
            return new OrderResponseDto(novaOrderm, this.recuperarInfoProdutos(ordemRequest.getIdProdutos()));
        } catch (Exception e) {
            log.error("Falha ao criar nova orderm para o usuario {}", ordemRequest.getEmailOrder(), e);
            throw new RuntimeException("Falha ao criar nova ordem");
        }
    }


    @Override
    public OrderResponseDto atualizarOrdem(OrderStatus novoStatus, Long idOrdem) {
        try {
            final Optional<Order> optOrdem = this.repository.findById(idOrdem);
            if(optOrdem.isPresent()) {
                final Order order = optOrdem.get();
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
            final ResponseEntity<List> produtos = this.webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("produtos", idProdutos)
                            .build())
                    .exchangeToMono(response -> {
                        if (HttpStatus.OK.equals(response.statusCode())) {
                            return response.toEntity(List.class);
                        } else {
                            throw new RuntimeException("Falha ao recuperar informacoes do produto");
                        }
                    }).block();
            if(produtos.getBody() != null) {
                return (List<InfoProdutos>) produtos.getBody();
            } else {
                throw new RuntimeException("Nao foi retornada informacao dos produtos");
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar informacoes dos produtos. Sera retornada lista vazia", e);
            return new ArrayList<>();
        }
    }

}