package br.edu.infnet.order.services.impl;

import br.edu.infnet.order.services.GenericCrudService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class GenericCrudServiceImpl<T, I, R extends JpaRepository<T, I>> implements GenericCrudService<T> {

    protected R repository;

    protected GenericCrudServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public T findById(Long id) {
        return this.repository.findById((I) id).orElseThrow();
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
        this.repository.deleteById((I) id);
    }
}