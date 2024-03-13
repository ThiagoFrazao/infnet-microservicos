package br.edu.infnet.order.dto.request;

import br.edu.infnet.order.domain.TipoPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {

    private List<Long> idProdutos;

    private String emailUsuario;

    private TipoPagamento tipoPagamento;

    private RequestStatus status;

    @JsonIgnore
    public boolean isRequisicaoPagamento() {
        return RequestStatus.REQUEST_PAYMENT.equals(this.status);
    }

}