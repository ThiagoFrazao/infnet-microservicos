package com.store.auth.controller.impl;

import com.store.auth.controller.GenericController;
import com.store.auth.domain.User;
import com.store.auth.service.impl.UsuarioServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UsuarioControllerImpl extends GenericController<User> {
    public UsuarioControllerImpl(UsuarioServiceImpl service) {
        super(service);
    }

}
