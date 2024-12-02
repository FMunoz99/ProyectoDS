package backend.auth.domain;

import backend.admin.domain.Admin;
import backend.auth.dto.AuthResponseDto;
import backend.auth.dto.LoginRequestDto;
import backend.auth.dto.RegisterRequestDto;
import backend.auth.exceptions.UserAlreadyExistException;
import backend.config.JwtService;
import backend.empleado.domain.Empleado;
import backend.estudiante.domain.Estudiante;
import backend.events.email_event.EmpleadoCreatedEvent;
import backend.events.email_event.EstudianteCreatedEvent;
import backend.usuario.domain.Role;
import backend.usuario.domain.Usuario;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class AuthService {

    final private BaseUsuarioRepository<Usuario> usuarioRepository;
    final private JwtService jwtService;
    final private PasswordEncoder passwordEncoder;
    final private ModelMapper modelMapper;
    final private ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthService (BaseUsuarioRepository<Usuario> usuarioRepository,
                        JwtService jwtService, PasswordEncoder passwordEncoder,
                        ApplicationEventPublisher eventPublisher) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = new ModelMapper();
        this.eventPublisher = eventPublisher;
    }

    public AuthResponseDto login (LoginRequestDto req) {
        Optional<Usuario> usuario;
        usuario = usuarioRepository.findByEmail(req.getEmail());

        if (usuario.isEmpty()) throw new UsernameNotFoundException("El correo electrónico no está registrado");

        if (!passwordEncoder.matches(req.getPassword(), usuario.get().getPassword()))
            throw new IllegalArgumentException("La contraseña es incorrecta");

        AuthResponseDto response = new AuthResponseDto();

        response.setToken(jwtService.generateToken(usuario.get()));
        return response;
    }

    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(registerRequestDto.getEmail());
        if (usuario.isPresent()) throw new UserAlreadyExistException("El correo electrónico ya está registrado");

        AuthResponseDto response = new AuthResponseDto();

        if (registerRequestDto.getIsAdmin() != null && registerRequestDto.getIsAdmin()) {
            // Crear perfil de administrador
            Admin admin = new Admin(); // Cambiar a Admin en lugar de Usuario
            admin.setCreatedAt(ZonedDateTime.now());
            admin.setRole(Role.ADMIN);
            admin.setFirstName(registerRequestDto.getFirstName());
            admin.setLastName(registerRequestDto.getLastName());
            admin.setEmail(registerRequestDto.getEmail());
            admin.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            admin.setPhoneNumber(registerRequestDto.getPhone());
            admin.setUpdatedAt(ZonedDateTime.now());

            usuarioRepository.save(admin); // Esto creará un registro en users y admin
            response.setToken(jwtService.generateToken(admin));
            return response;
        } else if (registerRequestDto.getIsEmpleado()) {
            Empleado empleado = new Empleado();
            empleado.setCreatedAt(ZonedDateTime.now());
            empleado.setRole(Role.EMPLEADO);
            empleado.setFirstName(registerRequestDto.getFirstName());
            empleado.setLastName(registerRequestDto.getLastName());
            empleado.setEmail(registerRequestDto.getEmail());
            empleado.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            empleado.setPhoneNumber(registerRequestDto.getPhone());
            empleado.setUpdatedAt(ZonedDateTime.now());

            usuarioRepository.save(empleado);

            // Publicar evento de creación de empleado
            eventPublisher.publishEvent(new EmpleadoCreatedEvent(empleado, empleado.getEmail()));

            response.setToken(jwtService.generateToken(empleado));
            return response;
        } else {
            Estudiante estudiante = new Estudiante();
            estudiante.setCreatedAt(ZonedDateTime.now());
            estudiante.setRole(Role.ESTUDIANTE);
            estudiante.setFirstName(registerRequestDto.getFirstName());
            estudiante.setLastName(registerRequestDto.getLastName());
            estudiante.setEmail(registerRequestDto.getEmail());
            estudiante.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            estudiante.setPhoneNumber(registerRequestDto.getPhone());
            estudiante.setUpdatedAt(ZonedDateTime.now());

            usuarioRepository.save(estudiante);

            // Publicar evento de creación de estudiante
            eventPublisher.publishEvent(new EstudianteCreatedEvent(estudiante, estudiante.getEmail()));

            response.setToken(jwtService.generateToken(estudiante));
            return response;
        }
    }


}
