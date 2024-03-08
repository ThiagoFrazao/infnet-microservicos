package br.edu.infnet.products.services.interfaces;

import br.edu.infnet.products.domain.Produto;
import br.edu.infnet.products.services.GenericCrudService;

public interface ProdutoGenericService extends GenericCrudService<Produto> {

    Produto atualizarProduto(Produto produto);

}