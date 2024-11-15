package backend.empleado.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.dto.EmpleadoPatchRequestDto;
import backend.empleado.dto.EmpleadoRequestDto;
import backend.empleado.dto.EmpleadoResponseDto;
import backend.empleado.dto.EmpleadoSelfResponseDto;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.EmpleadoCreatedEvent;
import backend.events.email_event.EmpleadoUpdatedEvent;
import backend.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService {

    final private EmpleadoRepository empleadoRepository;
    final private AuthorizationUtils authorizationUtils;
    final private ModelMapper modelMapper = new ModelMapper();
    final private ApplicationEventPublisher eventPublisher;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository, AuthorizationUtils authorizationUtils,
                           ApplicationEventPublisher eventPublisher) {
        this.empleadoRepository = empleadoRepository;
        this.authorizationUtils = authorizationUtils;
        this.eventPublisher = eventPublisher;
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


    public void deleteEmpleado(Long id) {
        // Verifica si el usuario autenticado es un administrador
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para eliminar este recurso");
        }

        empleadoRepository.deleteById(id);
    }


    public EmpleadoResponseDto updateEmpleadoInfo(Long id, EmpleadoPatchRequestDto empleadoInfo) {
        if (!authorizationUtils.isAdminOrResourceOwner(id)) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        }

        Empleado empleado = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        // Actualiza solo los campos que han sido enviados en el DTO
        if (empleadoInfo.getFirstName() != null) {
            empleado.setFirstName(empleadoInfo.getFirstName());
        }
        if (empleadoInfo.getLastName() != null) {
            empleado.setLastName(empleadoInfo.getLastName());
        }
        if (empleadoInfo.getPhoneNumber() != null) {
            empleado.setPhoneNumber(empleadoInfo.getPhoneNumber());
        }
        if (empleadoInfo.getEmail() != null) {
            empleado.setEmail(empleadoInfo.getEmail());
        }
        if (empleadoInfo.getHorarioDeTrabajo() != null) {
            empleado.setHorarioDeTrabajo(empleadoInfo.getHorarioDeTrabajo());
        }

        Empleado updatedEmpleado = empleadoRepository.save(empleado);

        String recipientEmail = updatedEmpleado.getEmail();
        EmpleadoUpdatedEvent event = new EmpleadoUpdatedEvent(updatedEmpleado, recipientEmail);
        eventPublisher.publishEvent(event);

        return modelMapper.map(updatedEmpleado, EmpleadoResponseDto.class);
    }


}
