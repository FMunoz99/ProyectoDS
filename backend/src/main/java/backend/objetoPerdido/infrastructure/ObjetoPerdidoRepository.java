package backend.objetoPerdido.infrastructure;

import backend.estudiante.domain.Estudiante;
import backend.objetoPerdido.domain.ObjetoPerdido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjetoPerdidoRepository extends JpaRepository<ObjetoPerdido, Long> {
    List<ObjetoPerdido> findByEstudiante(Estudiante estudiante);
}
