package br.edu.infnet.shopping.dto.request;

import br.edu.infnet.shopping.domain.TipoPagamento;
import lombok.Getter;

@Getter
public class PaymentRequestDto {

    private String orderId;

    private String emailUsuario;

    private TipoPagamento tipoPagamento;

}
