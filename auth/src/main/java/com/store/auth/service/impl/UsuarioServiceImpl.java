package com.store.auth.service.impl;

import com.store.auth.domain.User;
import com.store.auth.repository.UserRepository;
import com.store.auth.service.interfaces.UserGenericService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl extends GenericServiceImpl<User, Long, UserRepository> implements UserGenericService {
    protected UsuarioServiceImpl(UserRepository repository) {
        super(repository);
    }

}