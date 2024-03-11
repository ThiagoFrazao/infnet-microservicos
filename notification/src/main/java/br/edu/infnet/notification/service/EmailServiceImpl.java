package br.edu.infnet.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl {

    private final JavaMailSender emailSender;

    @Value("${mail.infnet.username}")
    private String userEmail;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String conteudo, String email, String titulo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(this.userEmail);
        msg.setSubject(titulo);
        msg.setText(conteudo);
        msg.setTo(email);
        this.emailSender.send(msg);
    }

}
