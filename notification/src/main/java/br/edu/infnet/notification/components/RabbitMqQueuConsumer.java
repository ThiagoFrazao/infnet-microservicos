package br.edu.infnet.notification.components;

import br.edu.infnet.notification.dto.EmailNotificationQueueDto;
import br.edu.infnet.notification.exception.ReadValueFromQueueException;
import br.edu.infnet.notification.service.EmailServiceImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqQueuConsumer {

    private final EmailServiceImpl emailService;

    private final ObjectMapper objectMapper;

    @Value("${rabbit.email_notification.queue.name}")
    private String queueName;

    public RabbitMqQueuConsumer(EmailServiceImpl emailService){
        this.emailService = emailService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
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
        } catch (JsonParseException | JsonMappingException e) {
          log.error("JSON invalido recebido na fila {}", this.queueName, e);
          throw new ReadValueFromQueueException("JSON invalido recebido na fila %s".formatted(this.queueName), e);
        } catch (Exception e) {
            try {
                final JsonNode node = this.objectMapper.readTree(message);
                log.error("Falha ao converter objeto {} para EmailNotificationQueueDto.class",
                        this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node),
                        e);
                throw new ReadValueFromQueueException("Falha ao converter objeto para EmailNotificationQueueDto.class", e);
            } catch (Exception ex) {
                log.error("Falha ao tentar formatar JSON para log.", e);
                throw new ReadValueFromQueueException("Falha ao tentar formatar JSON para log.", e);
            }
        }
    }

}
