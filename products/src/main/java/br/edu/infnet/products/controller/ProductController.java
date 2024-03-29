package br.edu.infnet.products.controller;

import br.edu.infnet.products.domain.Produto;
import br.edu.infnet.products.services.impl.produto.ProdutoCrudServiceImpl;
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

@RestController
@RequestMapping
public class ProductController {

    private final ProdutoCrudServiceImpl service;

    public ProductController(ProdutoCrudServiceImpl service) {
        this.service = service;
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

}