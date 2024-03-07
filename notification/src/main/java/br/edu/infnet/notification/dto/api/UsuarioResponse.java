package br.edu.infnet.notification.dto.api;

import lombok.Getter;

@Getter
public class UsuarioResponse {

    private String email;

    private String nome;

    private RoleUsuario role;

}