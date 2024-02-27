package com.store.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.notification.dto.EmailRabbitMqDTO;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailServiceImpl {

    private final JavaMailSender emailSender;

    private final WebClient webClient;

    @Value("${mail.infnet.username}")
    private String userEmail;

    public EmailServiceImpl(JavaMailSender emailSender, WebClient webClient){
        this.emailSender = emailSender;
        this.webClient = webClient;
    }

    public EmailRabbitMqDTO convertToObject(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, EmailRabbitMqDTO.class);
    }

    public String constructContent() {
        return "";
    }

    public void sendEmail(String content, String email, String subject) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(this.userEmail);
            msg.setSubject(subject);
            msg.setText(content);
            msg.setTo(email);
            this.emailSender.send(msg);

            final MimeMessageHelper messageHelper = this.createMessageHelper();
            messageHelper.setFrom(this.userEmail);
            messageHelper.setText(content);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            this.emailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            log.error("Falha ao enviar email");
        }
    }

    private MimeMessageHelper createMessageHelper() throws MessagingException {
        return new MimeMessageHelper(this.emailSender.createMimeMessage(), false, StandardCharsets.UTF_8.name());
    }

}
