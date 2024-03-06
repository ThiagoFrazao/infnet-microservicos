package br.edu.infnet.authorization.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorizationError {

    private final String mensagemFalha;

    private final ErrorType tipo;

}