package com.store.order.service.impl;

import com.store.order.service.GenericService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class GenericServiceImpl<T, ID, R extends JpaRepository<T, ID>> implements GenericService<T> {

    protected R repository;


    protected GenericServiceImpl(R repository) {
        this.repository = repository;
    }


    @Override
    public T findById(Long id) {
        return this.repository.findById((ID) id).orElseThrow();
    }

    @Override
    public List<T> findAll() {
        return this.repository.findAll();
    }

    @Override
    public void update(T entity) {
        this.repository.save(entity);
    }

    @Override
    public T save(T entity) {
        return this.repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById((ID) id);
    }

}
