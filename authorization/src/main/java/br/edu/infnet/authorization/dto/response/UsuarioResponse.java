package br.edu.infnet.authorization.dto.response;

import br.edu.infnet.authorization.domain.RoleUsuario;
import br.edu.infnet.authorization.domain.Usuario;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class UsuarioResponse {

    private final String email;

    private final String nome;

    private final RoleUsuario role;

    public UsuarioResponse(Usuario usuario) {
        this.email = StringUtils.trimToEmpty(usuario.getEmail());
        this.nome = StringUtils.trimToEmpty(usuario.getNome());
        this.role = usuario.getRole();
    }

}