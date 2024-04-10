package br.edu.infnet.products.services.impl.produto;

import br.edu.infnet.products.domain.Produto;
import br.edu.infnet.products.dto.MetricType;
import br.edu.infnet.products.exception.BusinessException;
import br.edu.infnet.products.repository.ProdutoRepository;
import br.edu.infnet.products.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.products.services.interfaces.ProdutoGenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProdutoCrudServiceImpl extends GenericCrudServiceImpl<Produto, Long, ProdutoRepository> implements ProdutoGenericService {

    protected ProdutoCrudServiceImpl(ProdutoRepository repository) {
        super(repository);
    }

    @Override
    public Produto atualizarProduto(Produto novoProduto) {
        final Produto produto = this.findById(novoProduto.getId());
        if(produto == null) {
            super.countErrorMetric(MetricType.ATUALIZAR_PRODUTO);
            throw new BusinessException(
                    "Falha ao atualizar produto. Produto ID %s nao encontrado".formatted(novoProduto.getId()),
                    "Verifique o numero do produto antes de tentar uma nova atualizacao.");
        } else {
            produto.setValor(novoProduto.getValor());
            produto.setNome(novoProduto.getNome());
            return this.repository.save(produto);
        }
    }

    @Override
    public List<Produto> recuperarProdutosPorId(List<Long> idProdutos) {
        try {
            if(idProdutos == null || idProdutos.isEmpty()) {
                return this.repository.findAll();
            } else {
                return this.repository.findAllById(idProdutos);
            }
        } catch (Exception e) {
            super.countErrorMetric(MetricType.RECUPERAR_POR_ID);
            throw e;
        }
    }

    @Override
    public Map<MetricType, Double> recuperarMetricas() {
        return super.recuperarMetricas();
    }

}