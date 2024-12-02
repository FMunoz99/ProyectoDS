package backend.admin.infrastructure;

import backend.admin.domain.Admin;
import backend.usuario.domain.Role;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface AdminRepository extends BaseUsuarioRepository<Admin> {

    @Query("SELECT a FROM Admin a WHERE a.email = :email")
    Optional<Admin> findByEmail(String email);
}
