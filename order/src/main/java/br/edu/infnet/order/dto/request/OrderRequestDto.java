package br.edu.infnet.order.dto.request;

import br.edu.infnet.order.domain.OrderStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {

    private List<Long> idProdutos;

    private String emailOrder;

    private OrderStatus status;

}