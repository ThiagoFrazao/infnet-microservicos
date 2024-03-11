package domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_item")
@NoArgsConstructor
public class OrderItem {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long idProduto;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    public OrderItem(Long produto, Order ordem) {
        this.idProduto = produto;
        this.order = ordem;
    }
}