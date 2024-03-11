package br.edu.infnet.order.dto.api.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioResponse {

    private String nome;

}