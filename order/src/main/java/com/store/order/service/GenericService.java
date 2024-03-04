package com.store.order.service;

import java.util.List;

public interface GenericService<T> {

    T findById(Long id);

    List<T> findAll();

    void update(T entity);

    T save(T entity);

    void delete(Long id);

}
