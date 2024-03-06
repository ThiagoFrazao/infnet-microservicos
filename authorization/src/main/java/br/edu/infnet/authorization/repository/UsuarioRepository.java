package br.edu.infnet.authorization.repository;

import br.edu.infnet.authorization.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findUsuarioByEmailIgnoreCase(String email);

}
