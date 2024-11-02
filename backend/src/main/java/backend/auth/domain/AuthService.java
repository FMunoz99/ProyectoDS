package backend.auth.domain;

import backend.auth.dto.AuthResponseDto;
import backend.auth.dto.LoginRequestDto;
import backend.auth.dto.RegisterRequestDto;
import backend.auth.exceptions.UserAlreadyExistException;
import backend.config.JwtService;
import backend.empleado.domain.Empleado;
import backend.estudiante.domain.Estudiante;
import backend.usuario.domain.Role;
import backend.usuario.domain.Usuario;
import backend.usuario.infrastructure.BaseUsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    final private BaseUsuarioRepository<Usuario> usuarioRepository;
    final private JwtService jwtService;
    final private PasswordEncoder passwordEncoder;
    final private ModelMapper modelMapper;

    @Autowired
    public AuthService (BaseUsuarioRepository<Usuario> usuarioRepository,
                        JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = new ModelMapper();
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

    public AuthResponseDto register (RegisterRequestDto registerRequestDto) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(registerRequestDto.getEmail());
        if (usuario.isPresent()) throw new UserAlreadyExistException("El correo electrónico ya está registrado");

        if (registerRequestDto.getIsEmpleado()) {
            Empleado empleado = new Empleado();
            empleado.setRole(Role.EMPLEADO);
            empleado.setFirstName(registerRequestDto.getFirstName());
            empleado.setLastName(registerRequestDto.getLastName());
            empleado.setEmail(registerRequestDto.getEmail());
            empleado.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            empleado.setPhoneNumber(registerRequestDto.getPhone());

            usuarioRepository.save(empleado);

            AuthResponseDto response = new AuthResponseDto();
            response.setToken(jwtService.generateToken(empleado));
            return response;
        } else {
            Estudiante estudiante = new Estudiante();
            estudiante.setRole(Role.ESTUDIANTE);
            estudiante.setFirstName(registerRequestDto.getFirstName());
            estudiante.setLastName(registerRequestDto.getLastName());
            estudiante.setEmail(registerRequestDto.getEmail());
            estudiante.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
            estudiante.setPhoneNumber(registerRequestDto.getPhone());

            usuarioRepository.save(estudiante);

            AuthResponseDto response = new AuthResponseDto();
            response.setToken(jwtService.generateToken(estudiante));
            return response;
        }
    }
}
