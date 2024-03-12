package br.edu.infnet.products.dto;

import lombok.Getter;

@Getter
public class ProductErrorResponseDto {

    private final String mensagemFalha;


    private final String detalhes;

    public ProductErrorResponseDto(String mensagemFalha, String detalhes) {
        this.mensagemFalha = mensagemFalha;
        this.detalhes = detalhes;
    }

    public ProductErrorResponseDto(String mensagemFalha) {
        this.mensagemFalha = mensagemFalha;
        this.detalhes = null;
    }
}