package com.store.notification.component.impl;

import com.store.notification.component.RabbitMqComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqComponentImpl implements RabbitMqComponent {
    @Override
    @RabbitListener(queues = "${rabbit.queue.name}")
    public void handleMessage(String message) {
        log.debug("MSG Recebida! Conteudo: {}", message);
    }
}
