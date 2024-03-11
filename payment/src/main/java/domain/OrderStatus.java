package domain;

import lombok.Getter;

@Getter
public enum OrderStatus {

    SHOPPING_CART,
    PROCESSING_PAYMENT,
    PAID,
    PROCESSING_DELIVERY,
    DELIVERED,
    PROCESSING_RETURN,
    RETURNED,
    CANCELED;

}