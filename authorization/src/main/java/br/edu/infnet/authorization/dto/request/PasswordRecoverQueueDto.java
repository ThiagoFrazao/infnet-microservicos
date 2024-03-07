package br.edu.infnet.authorization.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class PasswordRecoverQueueDto implements Serializable {

    private final String email;

    private final String conteudo;

    private final String titulo;

}