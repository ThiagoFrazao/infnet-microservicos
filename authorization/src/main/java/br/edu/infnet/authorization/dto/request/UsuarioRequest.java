package br.edu.infnet.authorization.dto.request;

import br.edu.infnet.authorization.domain.RoleUsuario;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class UsuarioRequest {

    @NotEmpty
    private String nome;

    @NotEmpty
    private RoleUsuario roleUsuario;

    @NotEmpty
    private String senha;

    @NotEmpty
    private String email;

    public String getNome() {
        return StringUtils.trimToNull(nome);
    }


    public String getSenha() {
        return StringUtils.trimToNull(senha);
    }

    public String getEmail() {
        return StringUtils.trimToNull(email);
    }
}