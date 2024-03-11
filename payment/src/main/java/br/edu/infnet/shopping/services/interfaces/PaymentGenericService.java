package br.edu.infnet.shopping.services.interfaces;


import br.edu.infnet.shopping.domain.Payment;
import br.edu.infnet.shopping.dto.request.PaymentRequestDto;
import br.edu.infnet.shopping.services.GenericCrudService;

public interface PaymentGenericService extends GenericCrudService<Payment> {

    Payment processarPagamento(PaymentRequestDto request);


}
