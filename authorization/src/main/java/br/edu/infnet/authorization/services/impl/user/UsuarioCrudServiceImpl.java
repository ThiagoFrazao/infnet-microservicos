package br.edu.infnet.authorization.services.impl.user;

import br.edu.infnet.authorization.domain.Usuario;
import br.edu.infnet.authorization.dto.request.UsuarioRequest;
import br.edu.infnet.authorization.repository.UsuarioRepository;
import br.edu.infnet.authorization.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.authorization.services.interfaces.UsuarioGenericService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioCrudServiceImpl extends GenericCrudServiceImpl<Usuario, Long, UsuarioRepository> implements UsuarioGenericService {
    private final PasswordEncoder passwordEncoder;
    private final AmqpTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    protected UsuarioCrudServiceImpl(UsuarioRepository repository, PasswordEncoder passwordEncoder, AmqpTemplate rabbitTemplate) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void atualizarUsuario(UsuarioRequest usuarioExistente) {
        try {
            final Usuario usuario = this.repository.findUsuarioByEmailIgnoreCase(usuarioExistente.getEmail());
            if(usuario == null) {
                //FIXME: implementar exception
                throw new RuntimeException("Usuario nao existente para realizar atualizacao");
            } else {
                if(this.passwordEncoder.matches(usuarioExistente.getSenha(), usuario.getSenha())) {
                    usuario.setSenha(this.passwordEncoder.encode(usuarioExistente.getSenha()));
                    usuario.setRole(usuarioExistente.getRoleUsuario());
                    usuario.setNome(usuarioExistente.getNome());
                    usuario.setEmail(usuarioExistente.getEmail());
                    this.repository.save(usuario);
                } else {
                    throw new RuntimeException("Senha invalida. Tente novamente.");
                }
            }
        } catch (Exception e) {
            //FIXME: implementar exception
            throw new RuntimeException();
        }
    }

    @Override
    public Usuario save(UsuarioRequest novoUsuario) {
        try {
            Usuario usuario = new Usuario();
            usuario.setRole(novoUsuario.getRoleUsuario());
            usuario.setNome(StringUtils.trimToNull(novoUsuario.getNome()));
            usuario.setEmail(StringUtils.trimToNull(novoUsuario.getEmail()));
            usuario.setSenha(this.passwordEncoder.encode(novoUsuario.getSenha()));
            return this.repository.save(usuario);
        } catch (Exception e) {
            //FIXME: implementar exception
            throw new RuntimeException("ALTERAR");
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        try {
            return this.repository.findUsuarioByEmailIgnoreCase(email);
        } catch (Exception e) {
            //FIXME: implementar exception
            throw new RuntimeException("ALTERAR");
        }
    }

    @Override
    public void delete(String email) {
        try {
            final Usuario usuario = this.repository.findUsuarioByEmailIgnoreCase(email);
            if(usuario == null) {
                throw new RuntimeException("Usuario nao existente para email %s".formatted(email));
            } else {
                this.repository.deleteById(usuario.getId());
            }
        } catch (Exception e) {
            //FIXME: implementar exception
            throw new RuntimeException("ALTERAR");
        }
    }

    @Override
    public void requisitarAlteracaoSenha(String email) {
        try {
            final Usuario usuario = this.repository.findUsuarioByEmailIgnoreCase(StringUtils.trimToNull(email));
            if(usuario == null) {
                throw new RuntimeException("Usuario nao encontrado para email %s".formatted(email));
            } else {
                this.rabbitTemplate.convertAndSend(exchange, routingKey, usuario.getSenha());
            }
        } catch (Exception e) {
            //FIXME: implementar exception
            throw new RuntimeException("ALTERAR");
        }
    }


}