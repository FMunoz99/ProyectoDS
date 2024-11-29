package backend.objetoPerdido.infrastructure;

import backend.estudiante.domain.Estudiante;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import backend.objetoPerdido.domain.ObjetoPerdido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjetoPerdidoRepository extends JpaRepository<ObjetoPerdido, Long> {
    List<ObjetoPerdido> findByEstudiante(Estudiante estudiante);

    List<ObjetoPerdido> findByEstadoReporte(EstadoReporte estadoReporte);

    List<ObjetoPerdido> findByEstadoTarea(EstadoTarea estadoTarea);

    List<ObjetoPerdido> findByEstudianteId(Long estudianteId);

    List<ObjetoPerdido> findByEmpleadoEmail(String email);

}
