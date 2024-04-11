package br.edu.infnet.products.controller;

import br.edu.infnet.products.domain.Produto;
import br.edu.infnet.products.dto.MetricType;
import br.edu.infnet.products.services.impl.produto.ProdutoCrudServiceImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ProductController {

    private final ProdutoCrudServiceImpl service;

    private final MeterRegistry meterRegistry;

    public ProductController(ProdutoCrudServiceImpl service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/new")
    public ResponseEntity<List<Produto>> salvarNovoProduto(@RequestBody List<Produto> requestBody) {
        return ResponseEntity.ok(this.service.saveAll(requestBody));
    }

    @PutMapping
    public ResponseEntity<Produto> atualizarProduto(@RequestBody Produto requestBody) {
        return ResponseEntity.ok(this.service.atualizarProduto(requestBody));
    }

    @GetMapping
    public ResponseEntity<List<Produto>> findAll(@RequestParam(name = "produtos", required = false) List<Long> idProdutos) {
        return ResponseEntity.ok(this.service.recuperarProdutosPorId(idProdutos));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/metrics/error")
    public ResponseEntity<Void> getErrorMetrics() {
        try {
            final Map<MetricType, Double> metricasService = this.service.recuperarMetricas();
            metricasService.forEach((i,j) -> {
                Counter counter = Counter.builder("ERROR_"+i.name())
                        .tag(i.name(), "produtos")
                        .description("Quantidade de erros %s para produtos".formatted(i.name()))
                        .register(this.meterRegistry);

                counter.increment(j);
            });
            return  ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/metrics/quantidade")
    public ResponseEntity<Void>  getQntProdutosMetric() {
        try {
            final List<Produto> produtos = this.service.findAll();
            Gauge.builder("quantidade_produtos_banco", produtos::size)
                    .description("Quantidade de produtos cadastrados na base")
                    .register(meterRegistry);
            return  ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}