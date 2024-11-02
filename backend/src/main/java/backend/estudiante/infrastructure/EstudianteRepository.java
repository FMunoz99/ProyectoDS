package backend.estudiante.infrastructure;

import backend.estudiante.domain.Estudiante;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface EstudianteRepository extends BaseUsuarioRepository<Estudiante> {
}
