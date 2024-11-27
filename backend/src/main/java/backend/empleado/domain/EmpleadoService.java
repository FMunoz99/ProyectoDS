package backend.empleado.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.dto.EmpleadoPatchRequestDto;
import backend.empleado.dto.EmpleadoRequestDto;
import backend.empleado.dto.EmpleadoResponseDto;
import backend.empleado.dto.EmpleadoSelfResponseDto;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.EmpleadoCreatedEvent;
import backend.events.email_event.EmpleadoUpdatedEvent;
import backend.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpleadoService {

    final private EmpleadoRepository empleadoRepository;
    final private AuthorizationUtils authorizationUtils;
    final private PasswordEncoder passwordEncoder;
    final private ModelMapper modelMapper = new ModelMapper();
    final private ApplicationEventPublisher eventPublisher;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository, AuthorizationUtils authorizationUtils,
                           ApplicationEventPublisher eventPublisher, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.authorizationUtils = authorizationUtils;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
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

        Empleado empleado = modelMapper.map(dto, Empleado.class);

        Empleado savedEmpleado = empleadoRepository.save(empleado);

        EmpleadoResponseDto responseDto = modelMapper.map(savedEmpleado, EmpleadoResponseDto.class);

        String recipientEmail = savedEmpleado.getEmail();
        EmpleadoCreatedEvent event = new EmpleadoCreatedEvent(savedEmpleado, recipientEmail);
        eventPublisher.publishEvent(event);

        return responseDto;
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

    public EmpleadoResponseDto updateEmpleadoInfo(Long id, EmpleadoPatchRequestDto empleadoInfo) {
        if (!authorizationUtils.isAdminOrResourceOwner(id)) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        }

        Empleado empleado = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        // Map para registrar los campos actualizados
        Map<String, String> updatedFields = new HashMap<>();

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
            updatedFields.put("Horario de Trabajo", empleadoInfo.getHorarioDeTrabajo().toString());
            empleado.setHorarioDeTrabajo(empleadoInfo.getHorarioDeTrabajo());
        }

        if (empleadoInfo.getPassword() != null && !empleadoInfo.getPassword().equals(empleado.getPassword())) {
            updatedFields.put("Contraseña", "Actualizada");
            empleado.setPassword(passwordEncoder.encode(empleadoInfo.getPassword()));
        }

        // Actualizar la fecha de modificación
        empleado.setUpdatedAt(ZonedDateTime.now());

        Empleado updatedEmpleado = empleadoRepository.save(empleado);

        String recipientEmail = updatedEmpleado.getEmail();
        EmpleadoUpdatedEvent event = new EmpleadoUpdatedEvent(updatedEmpleado, updatedFields, recipientEmail);
        eventPublisher.publishEvent(event);

        return modelMapper.map(updatedEmpleado, EmpleadoResponseDto.class);
    }



}
