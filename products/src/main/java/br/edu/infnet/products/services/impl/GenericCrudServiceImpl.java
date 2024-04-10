package br.edu.infnet.products.services.impl;

import br.edu.infnet.products.dto.MetricType;
import br.edu.infnet.products.services.GenericCrudService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericCrudServiceImpl<T, I, R extends JpaRepository<T, I>> implements GenericCrudService<T> {

    protected R repository;

    protected Map<MetricType, Double> errorMetricMap;

    protected GenericCrudServiceImpl(R repository) {
        this.repository = repository;
        this.errorMetricMap = this.gerarErrorMetricMap();
    }

    @Override
    public T findById(Long id) {
        try {
            return this.repository.findById((I) id).orElseThrow();
        } catch (Exception e) {
            this.countErrorMetric(MetricType.RECUPERAR_POR_ID);
            throw e;
        }
    }

    @Override
    public List<T> findAll() {
        try {
            return this.repository.findAll();
        } catch (Exception e) {
            this.countErrorMetric(MetricType.RECUPERAR_CONTEUDO);
            throw e;
        }
    }

    @Override
    public List<T> saveAll(List<T> entityList) {
        try {
            return this.repository.saveAll(entityList);
        } catch (Exception e) {
            this.countErrorMetric(MetricType.SALVAR_CONTEUDO);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            this.repository.deleteById((I) id);
        } catch (Exception e) {
            this.countErrorMetric(MetricType.REMOVER_CONTEUDO);
            throw e;
        }
    }

    protected void countErrorMetric(MetricType metricType) {
        this.errorMetricMap.put(metricType, this.errorMetricMap.get(metricType) + 1);
    }

    protected Map<MetricType, Double> recuperarMetricas() {
        return this.errorMetricMap;
    }

    private Map<MetricType, Double> gerarErrorMetricMap() {
        Map<MetricType, Double> returnMap = new HashMap<>();
        for(MetricType metricType : MetricType.values()) {
            returnMap.put(metricType, 0D);
        }
        return returnMap;
    }

}