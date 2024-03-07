package br.edu.infnet.notification.dto.api;

import lombok.Getter;

@Getter
public enum RoleUsuario {

    ADMIN,
    MANAGER,
    EMPLOYEE,
    CONSUMER,
    GUEST;

}