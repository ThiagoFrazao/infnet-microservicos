package br.edu.infnet.authorization.services;

import java.util.List;

public interface GenericCrudService<T> {

    T findById(Long id);

    List<T> findAll();

    void update(T entity);

    T save(T entity);

    void delete(Long id);

}
