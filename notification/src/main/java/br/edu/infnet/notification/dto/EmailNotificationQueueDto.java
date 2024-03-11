package br.edu.infnet.notification.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class EmailNotificationQueueDto implements Serializable {

    private String email;

    private String conteudo;

    private String titulo;

}