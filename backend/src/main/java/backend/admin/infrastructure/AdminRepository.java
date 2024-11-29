package backend.admin.infrastructure;

import backend.admin.domain.Admin;
import backend.usuario.domain.Role;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface AdminRepository extends BaseUsuarioRepository<Admin> {
}
