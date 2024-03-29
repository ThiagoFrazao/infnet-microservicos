package br.edu.infnet.products.services;

import java.util.List;

public interface GenericCrudService<T> {

    T findById(Long id);

    List<T> findAll();

    List<T> saveAll(List<T> entity);

    void delete(Long id);

}
