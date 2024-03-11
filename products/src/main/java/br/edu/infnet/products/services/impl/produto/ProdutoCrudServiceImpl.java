package br.edu.infnet.products.services.impl.produto;

import br.edu.infnet.products.domain.Produto;
import br.edu.infnet.products.exception.FalhaBancoDadosException;
import br.edu.infnet.products.repository.ProdutoRepository;
import br.edu.infnet.products.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.products.services.interfaces.ProdutoGenericService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
            throw new FalhaBancoDadosException(
                    "Falha ao atualizar produto. Produto ID %s nao encontrado".formatted(novoProduto.getId()));
        } else {
            produto.setValor(novoProduto.getValor());
            produto.setNome(novoProduto.getNome());
            return this.repository.save(produto);
        }
    }

    @Override
    public List<Produto> recuperarProdutosPorId(List<Long> idProdutos) {
        if(idProdutos == null || idProdutos.isEmpty()) {
            return this.repository.findAll();
        } else {
            return this.repository.findAllById(idProdutos);
        }
    }

}