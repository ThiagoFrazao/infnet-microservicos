package br.edu.infnet.shopping.dto.response;

import br.edu.infnet.shopping.domain.PaymentStatus;
import lombok.Getter;

@Getter
public class PaymentResponseDto {

    private final PaymentStatus status;

    public PaymentResponseDto(PaymentStatus status) {
        this.status = status;
    }

}