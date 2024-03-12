package br.edu.infnet.products.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String detalhes;

    public BusinessException(String mensagem, String detalhes) {
        super(mensagem);
        this.detalhes =  detalhes;
    }

}
