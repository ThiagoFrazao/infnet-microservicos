package br.edu.infnet.order.dto.response;

import br.edu.infnet.order.domain.Order;
import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.dto.api.products.InfoProdutos;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponseDto {

    private final String id;

    private final String emailUsuario;

    private final LocalDateTime dataCriacao;

    private final OrderStatus orderStatus;

    private final Double valorTotal;

    private final Integer totalItens;

    public OrderResponseDto(Order ordem, List<InfoProdutos> produtos) {
        this.id = ordem.getUuid().toString();
        this.emailUsuario = ordem.getEmailUsuario();
        this.dataCriacao = ordem.getDataCriacao();
        this.orderStatus = ordem.getStatus();
        this.valorTotal = produtos.stream().mapToDouble(InfoProdutos::getValor).sum();
        this.totalItens = produtos.size();
    }

}