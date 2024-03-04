package com.store.order.service.impl;

import com.store.order.domain.Order;
import com.store.order.domain.OrderItem;
import com.store.order.repository.OrderRepository;
import com.store.order.service.interfaces.OrderGenericService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderServiceImpl extends GenericServiceImpl<Order, Long, OrderRepository> implements OrderGenericService {

    private final WebClient webClient;

    protected OrderServiceImpl(OrderRepository repository, WebClient webClient) {
        super(repository);
        this.webClient = webClient;
    }

    @Override
    public Order save(Order novaOrdem) {
        if(novaOrdem.getOrderItems() != null) {
            for(OrderItem item : novaOrdem.getOrderItems()) {
                item.setOrder(novaOrdem);
            }
        }
        if(novaOrdem.getIdUsuario() != null) {
            this.webClient.get()
                    .uri("/")
        }
        return super.repository.save(novaOrdem);
    }

}