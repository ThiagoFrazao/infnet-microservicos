package br.edu.infnet.products.services.impl;

import br.edu.infnet.products.services.GenericCrudService;
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
    public List<T> saveAll(List<T> entityList) {
        return this.repository.saveAll(entityList);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById((I) id);
    }
}