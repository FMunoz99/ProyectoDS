package backend.usuario.infrastructure;

import backend.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseUsuarioRepository<T extends Usuario> extends JpaRepository<T, Long> {
    Optional<T> findByEmail(String email);
}
