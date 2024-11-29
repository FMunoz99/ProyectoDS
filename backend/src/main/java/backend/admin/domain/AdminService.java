package backend.admin.domain;

import backend.admin.dto.AdminPatchRequestDto;
import backend.admin.dto.AdminRequestDto;
import backend.admin.dto.AdminResponseDto;
import backend.admin.dto.AdminSelfResponseDto;
import backend.admin.infrastructure.AdminRepository;
import backend.auth.exceptions.UserAlreadyExistException;
import backend.empleado.domain.Empleado;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.EmpleadoCreatedEvent;
import backend.usuario.domain.Role;
import backend.exceptions.ResourceNotFoundException;
import backend.auth.utils.AuthorizationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    final private AdminRepository adminRepository;
    final private AuthorizationUtils authorizationUtils;
    final private ModelMapper modelMapper;
    final private PasswordEncoder passwordEncoder;
    private final EmpleadoRepository empleadoRepository;
    final private ApplicationEventPublisher eventPublisher;

    @Autowired
    public AdminService(AdminRepository adminRepository, AuthorizationUtils authorizationUtils,
                        ModelMapper modelMapper, PasswordEncoder passwordEncoder, EmpleadoRepository empleadoRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.adminRepository = adminRepository;
        this.authorizationUtils = authorizationUtils;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.empleadoRepository = empleadoRepository;
        this.eventPublisher = eventPublisher;
    }

    public AdminSelfResponseDto getAdminOwnInfo() {

        // Verificar si el usuario tiene el rol de administrador
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden crear administradores");
        }

        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null) {
            throw new UnauthorizeOperationException("Usuarios anónimos no tienen permiso de acceder a este recurso");
        }

        Admin admin = adminRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado"));

        return modelMapper.map(admin, AdminSelfResponseDto.class);
    }

    public AdminResponseDto createAdmin(AdminRequestDto adminRequestDto) {
        // Verificar si el usuario tiene el rol de administrador
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden crear administradores");
        }

        // Comprobación si ya existe un administrador con el mismo email
        if (adminRepository.findByEmail(adminRequestDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Administrador con email " + adminRequestDto.getEmail() + " ya existe.");
        }

        // Convertir AdminRequestDto a Admin
        Empleado admin = modelMapper.map(adminRequestDto, Empleado.class);

        // Encriptar la contraseña y asignar otros datos básicos
        admin.setPassword(passwordEncoder.encode(adminRequestDto.getPassword()));  // Encriptación de contraseña
        admin.setRole(Role.ADMIN);  // Asignamos el rol de administrador
        admin.setUpdatedAt(ZonedDateTime.now());
        admin.setCreatedAt(ZonedDateTime.now());

        // Guardar el administrador en la base de datos
        Empleado createAdmin = empleadoRepository.save(admin);

        // Publicar el evento de creación del empleado
        String recipientEmail = createAdmin.getEmail();
        EmpleadoCreatedEvent event = new EmpleadoCreatedEvent(createAdmin, recipientEmail);
        eventPublisher.publishEvent(event);

        // Convertir el administrador guardado a DTO y retornarlo
        return modelMapper.map(createAdmin, AdminResponseDto.class);
    }

    public AdminResponseDto updateAdmin(Long id, AdminPatchRequestDto adminPatchRequestDto) {
        Admin admin = adminRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        if (adminPatchRequestDto.getFirstName() != null) {
            admin.setFirstName(adminPatchRequestDto.getFirstName());
        }
        if (adminPatchRequestDto.getLastName() != null) {
            admin.setLastName(adminPatchRequestDto.getLastName());
        }
        if (adminPatchRequestDto.getPhoneNumber() != null) {
            admin.setPhoneNumber(adminPatchRequestDto.getPhoneNumber());
        }

        Admin updatedAdmin = adminRepository.save(admin);

        return modelMapper.map(updatedAdmin, AdminResponseDto.class);
    }

}
