package backend.usuario.domain;

import backend.admin.infrastructure.AdminRepository;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final BaseUsuarioRepository<Usuario> usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public UsuarioService(BaseUsuarioRepository<Usuario> usuarioRepository,  EstudianteRepository estudianteRepository,
                          EmpleadoRepository empleadoRepository, AdminRepository adminRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.empleadoRepository = empleadoRepository;
        this.adminRepository = adminRepository;
    }

    public Usuario findByEmail(String username, String role) {
        Usuario usuario;

        if (role.equals("ROLE_EMPLEADO")) {
            usuario = empleadoRepository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("Empleado no encontrado"));
        } else if (role.equals("ROLE_ADMIN")) {
            usuario = adminRepository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("Administrador no encontrado"));
        } else {
            usuario = estudianteRepository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("Estudiante no encontrado"));
        }

        return usuario;
    }

    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository
                    .findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return (UserDetails) usuario;
        };
    }

    public Estudiante getAuthenticatedEstudiante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        return estudianteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();  // El email se usa como el nombre de usuario
        } catch (ClassCastException e) {
            return null;
        }
    }

}
