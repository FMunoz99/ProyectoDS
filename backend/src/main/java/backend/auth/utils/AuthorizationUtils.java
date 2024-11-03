package backend.auth.utils;

import backend.empleado.domain.Empleado;
import backend.estudiante.domain.Estudiante;
import backend.usuario.domain.Role;
import backend.usuario.domain.Usuario;
import backend.usuario.domain.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtils {

    final private UsuarioService usuarioService;

    @Autowired
    public AuthorizationUtils(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public boolean isAdminOrResourceOwner(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().toArray()[0].toString();
        Usuario estudiante = usuarioService.findByEmail(username, role);

        return estudiante.getId().equals(id) || estudiante.getRole().equals(Role.ADMIN);
    }

    public boolean isAdminOrResourceOwner(Estudiante estudiante, Empleado empleado) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().toArray()[0].toString();

        Usuario usuario = usuarioService.findByEmail(username, role);

        return (estudiante != null && estudiante.getId().equals(usuario.getId())) ||
                (empleado != null && empleado.getId().equals(usuario.getId())) ||
                usuario.getRole().equals(Role.ADMIN);
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        } catch (ClassCastException e) {
            return null;
        }
    }
}
