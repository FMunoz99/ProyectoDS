package backend.incidente.infrastructure;

import backend.estudiante.domain.Estudiante;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import backend.incidente.domain.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {
    List<Incidente> findByEstudiante(Estudiante estudiante);

    List<Incidente> findByEstadoReporte(EstadoReporte estadoReporte);

    List<Incidente> findByEstadoTarea(EstadoTarea estadoTarea);

    List<Incidente> findByEstudianteId(Long estudianteId);
}
