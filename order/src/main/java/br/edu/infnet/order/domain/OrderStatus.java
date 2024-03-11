package br.edu.infnet.order.domain;

import br.edu.infnet.order.dto.api.payment.PaymentStatus;
import lombok.Getter;

@Getter
public enum OrderStatus {

    SHOPPING_CART,
    PROCESSING_PAYMENT,
    PAID,
    REJECTED_PAYMENT,
    FAILED_PAYMENT,
    PROCESSING_DELIVERY,
    DELIVERED,
    PROCESSING_RETURN,
    RETURNED,
    CANCELED;

    public static OrderStatus recoverStatusFromPaymentStatus(PaymentStatus status) {
        switch (status) {
            case PAID:
                return OrderStatus.PAID;
            case PROCESSING_PAYMENT:
                return PROCESSING_PAYMENT;
            case REJECTED:
                return REJECTED_PAYMENT;
            case FAILED:
            default:
                return CANCELED;
        }
    }
}