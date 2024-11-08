package backend.admin.infrastructure;

import backend.admin.domain.Admin;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface AdminRepository extends BaseUsuarioRepository<Admin> {
}
