package br.edu.infnet.products.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/probe")
@Slf4j
public class ProbesController {

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        try {
            return ResponseEntity.ok("Microservice Produtos esta saudavel.");
        } catch (Exception e) {
            log.error("Falha ao verificar se microservice Produtos esta saudavel", e);
            return ResponseEntity.internalServerError().body("Falha ao verificar microservice produtos");
        }
    }

    @GetMapping("/ready")
    public ResponseEntity<String> checkReady() {
        try {
           return ResponseEntity.ok("Microservice Produtos esta pronto.");
        } catch (Exception e) {
            log.error("Falha ao verificar se microservice Produtos esta pronto", e);
            return ResponseEntity.internalServerError().body("Falha ao verificar microservice produtos");
        }
    }

}