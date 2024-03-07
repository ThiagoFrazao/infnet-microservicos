package br.edu.infnet.notification.components;

import br.edu.infnet.notification.dto.queue.OrderNotificationQueueDto;
import br.edu.infnet.notification.dto.queue.PasswordRecoverQueueDto;
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

    @RabbitListener(queues = "${rabbit.recover_password.queue.name}")
    public void handleRecoverPasswordMessage(String message) {
        log.debug("MSG Recebida! Conteudo: {}", message);
        final PasswordRecoverQueueDto emailRabbitMqDTO = this.convertPasswordRecoverObject(message);
        this.emailService.sendEmail(emailRabbitMqDTO.getConteudo(), emailRabbitMqDTO.getEmail(), emailRabbitMqDTO.getTitulo());
    }

    @RabbitListener(queues = "${rabbit.notify_order.queue.name}")
    public void handleOrderNotificationMessage(String message) {
        log.debug("MSG Recebida! Conteudo: {}", message);
        final OrderNotificationQueueDto emailRabbitMqDTO = this.convertOrderNotifyObject(message);
        this.emailService.sendOrderNotificationEmail(
                emailRabbitMqDTO.getConteudo(),
                emailRabbitMqDTO.getIdUsuario(),
                emailRabbitMqDTO.getTitulo());
    }

    private OrderNotificationQueueDto convertOrderNotifyObject(String message) {
        try {
            return this.objectMapper.readValue(message, OrderNotificationQueueDto.class);
        } catch (Exception e) {
            log.error("FALHA AO CONVERTER ORDER NOTIFY OBJECT", e);
            throw new RuntimeException("Falha ao ");
        }
    }

    private PasswordRecoverQueueDto convertPasswordRecoverObject(String message) {
        try {
            return this.objectMapper.readValue(message, PasswordRecoverQueueDto.class);
        } catch (Exception e) {
            log.error("FALHA AO CONVERTER PASSWORD RECOVER OBJECT", e);
            throw new RuntimeException("Falha ao ");
        }
    }

}
