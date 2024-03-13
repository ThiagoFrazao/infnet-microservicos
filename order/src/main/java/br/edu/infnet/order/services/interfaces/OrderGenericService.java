package br.edu.infnet.order.services.interfaces;

import br.edu.infnet.order.domain.Order;
import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.dto.request.OrderRequestDto;
import br.edu.infnet.order.dto.response.OrderResponseDto;
import br.edu.infnet.order.services.GenericCrudService;

import java.util.List;

public interface OrderGenericService extends GenericCrudService<Order> {

    OrderResponseDto criarOrdem(OrderRequestDto usuarioExistente);

    OrderResponseDto atualizarOrdem(OrderStatus novoStatus, String idOrdem);

    OrderResponseDto removerProdutoOrdem(List<Long> removerProduto, String idOrdem);

    List<OrderResponseDto> recuperarTodasOrdensUsuario(String emailUsuario);

    Order findByUuid(String id);
}
