package br.edu.infnet.authorization.services.impl.user;

import br.edu.infnet.authorization.domain.Usuario;
import br.edu.infnet.authorization.dto.queue.EmailNotificationQueueDto;
import br.edu.infnet.authorization.dto.request.UsuarioRequest;
import br.edu.infnet.authorization.exception.BusinessException;
import br.edu.infnet.authorization.exception.DatabaseConnectionException;
import br.edu.infnet.authorization.exception.QueueServiceConnectionException;
import br.edu.infnet.authorization.repository.UsuarioRepository;
import br.edu.infnet.authorization.services.impl.GenericCrudServiceImpl;
import br.edu.infnet.authorization.services.interfaces.UsuarioGenericService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Value("${rabbit.exchange.name}")
    private String exchange;

    @Value("${rabbit.routing.key}")
    private String routingKey;

    private final ObjectMapper objectMapper;

    protected UsuarioCrudServiceImpl(UsuarioRepository repository, PasswordEncoder passwordEncoder, AmqpTemplate rabbitTemplate) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void atualizarUsuario(UsuarioRequest usuarioExistente) {
        try {
            final Usuario usuario = this.repository.findUsuarioByEmailIgnoreCase(usuarioExistente.getEmail());
            if(usuario == null) {
                log.error("Usuario {} nao encontrado no banco de dados.", usuarioExistente.getEmail());
                throw new BusinessException("Usuario nao existente para realizar atualizacao");
            } else {
                if(this.passwordEncoder.matches(usuarioExistente.getSenha(), usuario.getSenha())) {
                    usuario.setSenha(this.passwordEncoder.encode(usuarioExistente.getSenha()));
                    usuario.setRole(usuarioExistente.getRoleUsuario());
                    usuario.setNome(usuarioExistente.getNome());
                    usuario.setEmail(usuarioExistente.getEmail());
                    this.repository.save(usuario);
                } else {
                    log.error("Senha invalida para usuario {}", usuarioExistente.getEmail());
                    throw new BusinessException("Senha invalida. Tente novamente.");
                }
            }
        } catch (BusinessException e) {
          throw e;
        } catch (Exception e) {
            log.error("Falha ao conectar no banco de dados para salvar novo usuario {}", usuarioExistente.getEmail(), e);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
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
            log.error("Falha ao conectar no banco de dados para salvar novo usuario {}", novoUsuario.getEmail(), e);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        try {
            return this.repository.findUsuarioByEmailIgnoreCase(email);
        } catch (Exception e) {
            log.error("Falha ao conectar no banco de dados para recuperar usuario atraves do email {}", email, e);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public void delete(String email) {
        try {
            final Usuario usuario = this.repository.findUsuarioByEmailIgnoreCase(email);
            if(usuario == null) {
                throw new BusinessException("Usuario nao existente para email %s".formatted(email));
            } else {
                this.repository.deleteById(usuario.getId());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Falha ao conectar no banco de dados para deletar email {}.", email, e);
            throw new DatabaseConnectionException(this.repository.getClass().getName(), e);
        }
    }

    @Override
    public void requisitarAlteracaoSenha(String email) {
        try {
            final Usuario usuario = this.repository.findUsuarioByEmailIgnoreCase(StringUtils.trimToNull(email));
            if(usuario == null) {
                log.error("Usuario {} nao encontrado no banco de dados.", email);
                throw new BusinessException("Usuario nao encontrado para email %s".formatted(email));
            } else {
                this.rabbitTemplate.convertAndSend(
                        this.exchange,
                        this.routingKey,
                        this.objectMapper.writeValueAsString(new EmailNotificationQueueDto(email, this.gerarConteudoRecuperacaoSenha(usuario), this.gerarTituloRecuperacaoSenha(usuario))));
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new QueueServiceConnectionException(this.routingKey, e);
        }
    }

    private String gerarConteudoRecuperacaoSenha(Usuario usuario) {
        return "<h1>Atenção %s </h1>".formatted(usuario.getNome()) +
                "<p>Foi solicitada a alteração de senha para o seu email.</p>" +
                "<p>Se você não solicitou essa alteração entre em contato.</p>" +
                "<p>Se você solicitou clique no link abaixo</p>" +
                "<a href=\"https://www.google.com\" target=\"_blank\">Recuperar senha</a>";
    }

    private String gerarTituloRecuperacaoSenha(Usuario usuario) {
        return "Recuperacao email para %s".formatted(usuario.getNome());
    }


}