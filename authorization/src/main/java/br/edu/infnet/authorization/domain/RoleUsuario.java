package br.edu.infnet.authorization.domain;

import lombok.Getter;

@Getter
public enum RoleUsuario {

    ADMIN,
    MANAGER,
    EMPLOYEE,
    CONSUMER,
    GUEST;

}