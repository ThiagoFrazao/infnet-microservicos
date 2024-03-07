package br.edu.infnet.notification.dto.queue;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class OrderNotificationQueueDto implements Serializable {

    private Long idUsuario;

    private String conteudo;

    private String titulo;

}