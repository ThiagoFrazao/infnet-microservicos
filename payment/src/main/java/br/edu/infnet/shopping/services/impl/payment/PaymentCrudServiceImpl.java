package br.edu.infnet.shopping.services.impl.payment;

import br.edu.infnet.shopping.domain.Payment;
import br.edu.infnet.shopping.domain.PaymentStatus;
import br.edu.infnet.shopping.dto.request.PaymentRequestDto;
import br.edu.infnet.shopping.repository.PaymentRepository;
import br.edu.infnet.shopping.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.shopping.services.interfaces.PaymentGenericService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentCrudServiceImpl extends GenericCrudServiceImpl<Payment, Long, PaymentRepository> implements PaymentGenericService {


    protected PaymentCrudServiceImpl(PaymentRepository repository) {
        super(repository);
    }


    @Override
    public Payment processarPagamento(PaymentRequestDto request) {
        final Payment payment = new Payment();
        payment.setDataPagamento(LocalDateTime.now());
        try {
            payment.setTipoPagamento(request.getTipoPagamento());
            payment.setEmailUsuario(request.getEmailUsuario());
            payment.setStatus(this.enviarPagamentoApiExterna(request));
            return this.repository.save(payment);
        } catch (Exception e) {
            log.error("Falha ao processamento pagamento para ordem {}", request.getOrderId(), e);
            payment.setStatus(PaymentStatus.FAILED);
            return new Payment();
        }
    }

    /**
     * Metodo "mock" que deveria conectar a uma API externa para realizar o pagamento
     */
    private PaymentStatus enviarPagamentoApiExterna(PaymentRequestDto requestDto) {
        try {
            if(StringUtils.isNotBlank(requestDto.getOrderId()) && requestDto.getOrderId().length() > 3) {
                if(StringUtils.isNumeric(requestDto.getOrderId().substring(3, 4))) {
                    return PaymentStatus.PAID;
                } else {
                    return PaymentStatus.PROCESSING_PAYMENT;
                }
            } else {
                return PaymentStatus.REJECTED;
            }
        } catch (Exception e) {
            log.error("Falha ao enviar pagamento para api externa. Pagamento sera rejeitado.", e);
            return PaymentStatus.REJECTED;
        }
    }

}