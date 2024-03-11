package br.edu.infnet.order.dto.request;

import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.domain.TipoPagamento;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {

    private List<Long> idProdutos;

    private String emailUsuario;

    private TipoPagamento tipoPagamento;

    private OrderStatus status;

}