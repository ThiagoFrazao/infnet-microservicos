package br.edu.infnet.order.dto.api.payment;

import br.edu.infnet.order.domain.Order;
import br.edu.infnet.order.domain.TipoPagamento;
import lombok.Getter;

@Getter
public class PaymentRequestDto {

    private final String orderId;

    private final String emailUsuario;

    private final TipoPagamento tipoPagamento;

    public PaymentRequestDto(Order order) {
        this.orderId = order.getUuid().toString();
        this.emailUsuario = order.getEmailUsuario();
        this.tipoPagamento = order.getTipoPagamento();
    }
}
