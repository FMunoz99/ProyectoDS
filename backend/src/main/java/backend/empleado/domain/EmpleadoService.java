package backend.empleado.domain;

import backend.auth.exceptions.UserAlreadyExistException;
import backend.auth.utils.AuthorizationUtils;
import backend.aws_s3.StorageService;
import backend.empleado.dto.EmpleadoPatchRequestDto;
import backend.empleado.dto.EmpleadoRequestDto;
import backend.empleado.dto.EmpleadoResponseDto;
import backend.empleado.dto.EmpleadoSelfResponseDto;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.EmpleadoCreatedEvent;
import backend.events.email_event.EmpleadoUpdatedEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.incidente.domain.Incidente;
import backend.incidente.dto.IncidenteResponseDto;
import backend.incidente.infrastructure.IncidenteRepository;
import backend.objetoPerdido.domain.ObjetoPerdido;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import backend.objetoPerdido.infrastructure.ObjetoPerdidoRepository;
import backend.usuario.domain.Role;
import backend.usuario.domain.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmpleadoService {

    final private EmpleadoRepository empleadoRepository;
    final private AuthorizationUtils authorizationUtils;
    final private PasswordEncoder passwordEncoder;
    final private ModelMapper modelMapper = new ModelMapper();
    final private ApplicationEventPublisher eventPublisher;
    final private UsuarioService usuarioService;
    final private IncidenteRepository incidenteRepository;
    final private ObjetoPerdidoRepository objetoPerdidoRepository;
    final private StorageService storageService;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository, AuthorizationUtils authorizationUtils,
                           ApplicationEventPublisher eventPublisher, PasswordEncoder passwordEncoder,
                           UsuarioService usuarioService, IncidenteRepository incidenteRepository,
                           ObjetoPerdidoRepository objetoPerdidoRepository, StorageService storageService) {
        this.empleadoRepository = empleadoRepository;
        this.authorizationUtils = authorizationUtils;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
        this.incidenteRepository = incidenteRepository;
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.storageService = storageService;
    }

    public List<EmpleadoResponseDto> getAllEmpleados() {

        // Verificación del rol de usuario
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden ver la lista de empleados");
        }

        List<Empleado> empleados = empleadoRepository.findAll();
        return empleados.stream()
                .map(empleado -> modelMapper.map(empleado, EmpleadoResponseDto.class))
                .toList();
    }

    public EmpleadoResponseDto getEmpleadoInfo (Long id) {
        Empleado empleado = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        EmpleadoResponseDto response = new EmpleadoResponseDto();

        response.setId(empleado.getId());
        response.setFirstName(empleado.getFirstName());
        response.setLastName(empleado.getLastName());
        response.setEmail(empleado.getEmail());
        response.setPhoneNumber(empleado.getPhoneNumber());
        response.setHorarioDeTrabajo(empleado.getHorarioDeTrabajo());

        return response;
    }

    public EmpleadoResponseDto createEmpleado(EmpleadoRequestDto dto) {
        // Verificar si el usuario tiene el rol de administrador
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden crear empleados");
        }

        // Comprobación si el empleado con el mismo email ya existe
        if (empleadoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Empleado con email " + dto.getEmail() + " ya existe.");
        }

        // Convertir el EmpleadoRequestDto a Empleado
        Empleado empleado = modelMapper.map(dto, Empleado.class);

        // Encriptar la contraseña y asignar otros datos básicos
        empleado.setPassword(passwordEncoder.encode(dto.getPassword()));
        empleado.setRole(Role.EMPLEADO);  // Asignamos el rol de empleado
        empleado.setPhoneNumber(dto.getPhoneNumber());  // Asignamos el número de teléfono
        empleado.setUpdatedAt(ZonedDateTime.now());
        empleado.setCreatedAt(ZonedDateTime.now());

        // Guardar el empleado en la base de datos
        Empleado savedEmpleado = empleadoRepository.save(empleado);

        // Publicar el evento de creación del empleado
        String recipientEmail = savedEmpleado.getEmail();
        EmpleadoCreatedEvent event = new EmpleadoCreatedEvent(savedEmpleado, recipientEmail);
        eventPublisher.publishEvent(event);

        // Convertir el empleado guardado a DTO y retornarlo
        return modelMapper.map(savedEmpleado, EmpleadoResponseDto.class);
    }


    public EmpleadoSelfResponseDto getEmpleadoOwnInfo() {
        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null)
            throw new UnauthorizeOperationException("Usuarios anónimos no tienen permiso de acceder a este recurso");

        Empleado empleado = empleadoRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Empleado no encontrado"));
        return modelMapper.map(empleado, EmpleadoSelfResponseDto.class);
    }


    public ResponseEntity<String> deleteEmpleado(Long id) {
        // Verificar que el usuario tiene permisos para eliminar
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para eliminar este recurso");
        }

        // Verificar si el empleado existe
        if (!empleadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado con ID " + id + " no encontrado");
        }

        empleadoRepository.deleteById(id);

        // Retornar una respuesta con el mensaje de éxito
        return ResponseEntity.ok("Empleado con ID " + id + " eliminado con éxito");
    }

    public EmpleadoResponseDto updateEmpleadoInfo(Long id, EmpleadoPatchRequestDto empleadoInfo, MultipartFile fotoPerfil) throws IOException {
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        }

        Empleado empleado = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        // Map para registrar los campos actualizados
        Map<String, Object> updatedFields = new HashMap<>();

        if (empleadoInfo.getFirstName() != null && !empleadoInfo.getFirstName().equals(empleado.getFirstName())) {
            updatedFields.put("Nombre", empleadoInfo.getFirstName());
            empleado.setFirstName(empleadoInfo.getFirstName());
        }

        if (empleadoInfo.getLastName() != null && !empleadoInfo.getLastName().equals(empleado.getLastName())) {
            updatedFields.put("Apellido", empleadoInfo.getLastName());
            empleado.setLastName(empleadoInfo.getLastName());
        }

        if (empleadoInfo.getPhoneNumber() != null && !empleadoInfo.getPhoneNumber().equals(empleado.getPhoneNumber())) {
            updatedFields.put("Teléfono", empleadoInfo.getPhoneNumber());
            empleado.setPhoneNumber(empleadoInfo.getPhoneNumber());
        }

        if (empleadoInfo.getEmail() != null && !empleadoInfo.getEmail().equals(empleado.getEmail())) {
            updatedFields.put("Email", empleadoInfo.getEmail());
            empleado.setEmail(empleadoInfo.getEmail());
        }

        if (empleadoInfo.getHorarioDeTrabajo() != null && !empleadoInfo.getHorarioDeTrabajo().equals(empleado.getHorarioDeTrabajo())) {
            Map<String, String> horarioDeTrabajo = empleadoInfo.getHorarioDeTrabajo();
            // Convertir el horario a una cadena legible
            String horarioDeTrabajoString = horarioDeTrabajo.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining(", "));
            updatedFields.put("Horario de Trabajo", horarioDeTrabajoString);
            empleado.setHorarioDeTrabajo(horarioDeTrabajo);
        }

        if (empleadoInfo.getPassword() != null) {
            // Validar si la nueva contraseña es diferente antes de actualizar
            if (!passwordEncoder.matches(empleadoInfo.getPassword(), empleado.getPassword())) {
                updatedFields.put("Contraseña", "Actualizada");
                empleado.setPassword(passwordEncoder.encode(empleadoInfo.getPassword()));
            }
        }

        // Validar y actualizar la foto de perfil si es diferente
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String newFotoUrl = "empleados/" + empleado.getEmail();
            if (!newFotoUrl.equals(empleado.getFotoPerfilUrl())) {
                String fotoUrl = storageService.uploadFile(fotoPerfil, newFotoUrl);
                updatedFields.put("Foto de Perfil", "Actualizada");
                empleado.setFotoPerfilUrl(fotoUrl);
            }
        }

        // Actualizar la fecha de modificación solo si hay cambios
        if (!updatedFields.isEmpty()) {
            empleado.setUpdatedAt(ZonedDateTime.now());
            Empleado updatedEmpleado = empleadoRepository.save(empleado);

            // Enviar evento
            String recipientEmail = updatedEmpleado.getEmail();
            EmpleadoUpdatedEvent event = new EmpleadoUpdatedEvent(updatedEmpleado, updatedFields, recipientEmail);
            eventPublisher.publishEvent(event);

            return modelMapper.map(updatedEmpleado, EmpleadoResponseDto.class);
        }

        // Retornar el empleado sin cambios si no hubo actualizaciones
        return modelMapper.map(empleado, EmpleadoResponseDto.class);
    }


    // NUEVOS SERVICIOS

    // Metodo para obtener los incidentes asignados al empleado autenticado
    public List<IncidenteResponseDto> getIncidentesAsignados() {

        if (!authorizationUtils.isEmpleado()) {
            throw new ResourceNotFoundException("Solo los empleados pueden acceder a este recurso");
        }

        String email = usuarioService.getCurrentUserEmail();
        List<Incidente> incidentes = incidenteRepository.findByEmpleadoEmail(email);

        return incidentes.stream()
                .map(incidente -> modelMapper.map(incidente, IncidenteResponseDto.class))
                .collect(Collectors.toList());
    }

    // Metodo para obtener los objetos perdidos asignados al empleado autenticado
    public List<ObjetoPerdidoResponseDto> getObjetosPerdidosAsignados() {
        if (!authorizationUtils.isEmpleado()) {
            throw new ResourceNotFoundException("Solo los empleados pueden acceder a este recurso");
        }

        String email = usuarioService.getCurrentUserEmail();
        List<ObjetoPerdido> objetosPerdidos = objetoPerdidoRepository.findByEmpleadoEmail(email);

        return objetosPerdidos.stream()
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .collect(Collectors.toList());
    }
}
