package br.edu.infnet.notification.dto.queue;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class PasswordRecoverQueueDto implements Serializable {

    private String email;

    private String conteudo;

    private String titulo;

}