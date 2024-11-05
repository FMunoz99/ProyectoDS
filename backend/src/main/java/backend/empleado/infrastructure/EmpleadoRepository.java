package backend.empleado.infrastructure;

import backend.empleado.domain.Empleado;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface EmpleadoRepository extends BaseUsuarioRepository<Empleado> {

    @Query("SELECT e.email FROM Empleado e")
    List<String> findAllEmpleadosEmails();

}
