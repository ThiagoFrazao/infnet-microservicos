package br.edu.infnet.shopping.controller;

import br.edu.infnet.shopping.domain.PaymentStatus;
import br.edu.infnet.shopping.dto.request.PaymentRequestDto;
import br.edu.infnet.shopping.dto.response.PaymentResponseDto;
import br.edu.infnet.shopping.services.impl.payment.PaymentCrudServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class PaymentController {

    private final PaymentCrudServiceImpl service;

    public PaymentController(PaymentCrudServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> processarPagamento(@RequestBody PaymentRequestDto requestBody) {
        final PaymentStatus paymentStatus = this.service.processarPagamento(requestBody).getStatus();
        return ResponseEntity.status(this.getHttpStatusFromPaymentStatus(paymentStatus))
                .body(new PaymentResponseDto(paymentStatus));
    }

    private HttpStatus getHttpStatusFromPaymentStatus(PaymentStatus paymentStatus) {
        switch (paymentStatus) {
            case PROCESSING_PAYMENT, PAID:
                return HttpStatus.OK;
            case REJECTED:
                return HttpStatus.BAD_REQUEST;
            case FAILED:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}