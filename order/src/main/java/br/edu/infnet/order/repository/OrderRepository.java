package br.edu.infnet.order.repository;

import br.edu.infnet.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByEmailUsuarioIgnoreCase(String emailUsuario);

    Order findByUuid(UUID uuid);

}
