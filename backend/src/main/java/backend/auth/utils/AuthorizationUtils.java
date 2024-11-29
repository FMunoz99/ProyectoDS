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

    public boolean isAdminOrEmpleado(Empleado empleado) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().toArray()[0].toString();

        // Obtiene el usuario autenticado a partir de su correo electrónico y rol
        Usuario usuario = usuarioService.findByEmail(username, role);

        // Permitir si es ADMIN o si el usuario autenticado es un EMPLEADO
        return usuario.getRole().equals(Role.ADMIN) || (empleado != null && empleado.getId().equals(usuario.getId()));
    }

    public boolean isAdminOrEmpleado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().toArray()[0].toString(); // Asumimos que hay un solo rol

        // Solo permite acceso si el rol es ADMIN o EMPLEADO
        return role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLEADO");
    }

    public boolean isEstudiante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Verifica si el usuario tiene el rol 'ROLE_ESTUDIANTE'
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ESTUDIANTE"));
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Verifica si el usuario tiene el rol 'ROLE_ADMIN'
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    // Verifica si el usuario autenticado tiene el rol de EMPLEADO
    public boolean isEmpleado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().toArray()[0].toString(); // Obtiene el primer rol asignado (asumimos que hay uno solo)

        // Verifica si el rol del usuario es "ROLE_EMPLEADO"
        return role.equals("ROLE_EMPLEADO");
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
