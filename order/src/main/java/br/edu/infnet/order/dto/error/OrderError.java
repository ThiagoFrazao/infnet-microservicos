package br.edu.infnet.order.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderError {

    private final String mensagemFalha;


}