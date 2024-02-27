package com.store.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailRabbitMqDTO {


    private String email;

    private String conteudo;

    private String titulo;


}
