package backend.estudiante.infrastructure;

import backend.estudiante.domain.Estudiante;
import backend.incidente.domain.Incidente;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface EstudianteRepository extends BaseUsuarioRepository<Estudiante> {
}
