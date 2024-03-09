package br.edu.infnet.order.controller;

import br.edu.infnet.order.domain.Order;
import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.dto.request.OrderRequestDto;
import br.edu.infnet.order.dto.response.OrderResponseDto;
import br.edu.infnet.order.services.impl.user.OrderCrudServiceImpl;
import org.springframework.http.ResponseEntity;
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
public class OrderController {

    private final OrderCrudServiceImpl service;

    public OrderController(OrderCrudServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> criarOrder(@RequestBody OrderRequestDto requestBody) {
        return ResponseEntity.ok(this.service.criarOrdem(requestBody));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> atualizarOrdem(@PathVariable Long id, @RequestParam(name = "status") OrderStatus status) {
        return ResponseEntity.ok(this.service.atualizarOrdem(status, id));
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<OrderResponseDto>> recuperarOrdensUsuario(@PathVariable String email) {
        return ResponseEntity.ok(this.service.recuperarTodasOrdensUsuario(email));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id));
    }

}