package com.store.order.controller.impl;

import com.store.order.controller.GenericController;
import com.store.order.domain.Order;
import com.store.order.service.impl.OrderServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderControllerImpl extends GenericController<Order> {
    public OrderControllerImpl(OrderServiceImpl service) {
        super(service);
    }

}