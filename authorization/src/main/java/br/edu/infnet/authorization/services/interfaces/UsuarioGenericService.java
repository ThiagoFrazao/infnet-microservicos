package br.edu.infnet.authorization.services.interfaces;

import br.edu.infnet.authorization.domain.Usuario;
import br.edu.infnet.authorization.dto.request.UsuarioRequest;
import br.edu.infnet.authorization.services.GenericCrudService;

public interface UsuarioGenericService extends GenericCrudService<Usuario> {

    void atualizarUsuario(UsuarioRequest usuarioExistente);

    Usuario save(UsuarioRequest novoUsuario);

    Usuario findByEmail(String email);

    void delete(String email);

    void requisitarAlteracaoSenha(String email);

}
