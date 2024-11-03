package backend.incidente.infrastructure;

import backend.estudiante.domain.Estudiante;
import backend.incidente.domain.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {
    List<Incidente> findByEstudiante(Estudiante estudiante);
}
