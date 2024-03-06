package br.edu.infnet.authorization.controller;

import br.edu.infnet.authorization.dto.request.UsuarioRequest;
import br.edu.infnet.authorization.dto.response.UsuarioResponse;
import br.edu.infnet.authorization.services.impl.user.UsuarioCrudServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UsuarioController  {

    private final UsuarioCrudServiceImpl service;

    public UsuarioController(UsuarioCrudServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> salvarNovoUsuario(@RequestBody UsuarioRequest requestBody) {
        return ResponseEntity.ok(new UsuarioResponse(this.service.save(requestBody)));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAll().stream().map(UsuarioResponse::new).toList());
    }

    @GetMapping("/{email}")
    public ResponseEntity<UsuarioResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(new UsuarioResponse(this.service.findByEmail(email)));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteByEmail(@PathVariable String email) {
        this.service.delete(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recover-password/{email}")
    public ResponseEntity<Void> recuperarSenha(@PathVariable String email) {
        this.service.requisitarAlteracaoSenha(email);
        return ResponseEntity.noContent().build();
    }

}