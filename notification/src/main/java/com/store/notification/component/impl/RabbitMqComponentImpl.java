package com.store.notification.component.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.store.notification.component.RabbitMqComponent;
import com.store.notification.dto.EmailRabbitMqDTO;
import com.store.notification.service.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqComponentImpl implements RabbitMqComponent {

    private final EmailServiceImpl emailService;

    public RabbitMqComponentImpl(EmailServiceImpl emailService){
        this.emailService = emailService;
    }

    @Override
    @RabbitListener(queues = "${rabbit.queue.name}")
    public void handleMessage(String message) {
        try {
            log.debug("MSG Recebida! Conteudo: {}", message);
            final EmailRabbitMqDTO emailRabbitMqDTO = this.emailService.convertToObject(message);
            this.emailService.sendEmail(emailRabbitMqDTO.getConteudo(), emailRabbitMqDTO.getEmail(), emailRabbitMqDTO.getTitulo());
        } catch (JsonProcessingException e) {
            log.error("Falha ao trabalhar com mensagem do RabbitMQ ", e);
        }

    }
}
