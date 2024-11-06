package backend.noticias.infrastructure;

import backend.noticias.domain.Noticias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticiasRepository extends JpaRepository<Noticias, Long> {
}
