package br.edu.infnet.order.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class EmailNotificationQueueDto implements Serializable {

    private final String email;

    private final String conteudo;

    private final String titulo;

}