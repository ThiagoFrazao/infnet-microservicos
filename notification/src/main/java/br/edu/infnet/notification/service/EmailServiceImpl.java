package br.edu.infnet.notification.service;

import br.edu.infnet.notification.dto.api.UsuarioResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class EmailServiceImpl {

    private final JavaMailSender emailSender;

    private final WebClient webClient;


    @Value("${mail.infnet.username}")
    private String userEmail;

    public EmailServiceImpl(JavaMailSender emailSender, WebClient webClient) {
        this.emailSender = emailSender;
        this.webClient = webClient;
    }

    public void sendEmail(String conteudo, String email, String titulo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(this.userEmail);
        msg.setSubject(titulo);
        msg.setText(conteudo);
        msg.setTo(email);
        this.emailSender.send(msg);
    }

    public void sendOrderNotificationEmail(String conteudo, Long idUsuario, String titulo) {
        final ResponseEntity<UsuarioResponse> infoUsuario = this.webClient.get()
                .uri("/user/%s".formatted(idUsuario))
                .exchangeToMono(response -> {
                    if (HttpStatus.OK.equals(response.statusCode())) {
                        return response.toEntity(UsuarioResponse.class);
                    } else {
                        throw new RuntimeException("Falha ao recuperar informacoes do usuario");
                    }
                }).block();
        if(infoUsuario != null && infoUsuario.hasBody()) {
            this.sendEmail(conteudo, infoUsuario.getBody().getEmail(), titulo);
        } else {
            log.error("Info usuario nao retornada para id {}", idUsuario);
            throw new RuntimeException("FALHA ENVIAR NOTIFICACAO DE ORDEM");
        }

    }
}
