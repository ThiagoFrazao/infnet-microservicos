package br.edu.infnet.shopping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "payment")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String emailUsuario;

    @Column(nullable = false)
    private LocalDateTime dataPagamento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

}