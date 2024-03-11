package br.edu.infnet.notification.components;

import br.edu.infnet.notification.dto.EmailNotificationQueueDto;
import br.edu.infnet.notification.service.EmailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqQueuConsumer {

    private final EmailServiceImpl emailService;

    private final ObjectMapper objectMapper;

    public RabbitMqQueuConsumer(EmailServiceImpl emailService){
        this.emailService = emailService;
        objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = "${rabbit.email_notification.queue.name}")
    public void handleRecoverPasswordMessage(String message) {
        log.debug("MSG Recebida! Conteudo: {}", message);
        final EmailNotificationQueueDto emailRabbitMqDTO = this.convertPasswordRecoverObject(message);
        this.emailService.sendEmail(emailRabbitMqDTO.getConteudo(), emailRabbitMqDTO.getEmail(), emailRabbitMqDTO.getTitulo());
    }

    private EmailNotificationQueueDto convertPasswordRecoverObject(String message) {
        try {
            return this.objectMapper.readValue(message, EmailNotificationQueueDto.class);
        } catch (Exception e) {
            log.error("FALHA AO CONVERTER PASSWORD RECOVER OBJECT", e);
            throw new RuntimeException("Falha ao recuperar objeto da fila de notificacao de email");
        }
    }

}
