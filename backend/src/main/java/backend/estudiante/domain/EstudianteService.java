package backend.estudiante.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.estudiante.dto.EstudiantePatchRequestDto;
import backend.estudiante.dto.EstudianteRequestDto;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.dto.EstudianteSelfResponseDto;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.events.email_event.EstudianteCreatedEvent;
import backend.events.email_event.EstudianteUpdatedEvent;
import backend.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public EstudianteService(EstudianteRepository estudianteRepository, ModelMapper modelMapper,
                             AuthorizationUtils authorizationUtils, ApplicationEventPublisher eventPublisher) {
        this.estudianteRepository = estudianteRepository;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.eventPublisher = eventPublisher;
    }

    public List<EstudianteResponseDto> getAllEstudiantes() {

        // Verificación del rol de usuario
        if (!authorizationUtils.isAdminOrEmpleado()) {
            throw new UnauthorizeOperationException("Solo los administradores o empleados pueden ver la lista de estudiantes");
        }

        List<Estudiante> estudiantes = estudianteRepository.findAll();
        return estudiantes.stream()
                .map(estudiante -> modelMapper.map(estudiante, EstudianteResponseDto.class))
                .toList();
    }

    public EstudianteResponseDto createEstudiante(EstudianteRequestDto dto) {

        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden crear estudiantes");
        }
        Estudiante estudiante = modelMapper.map(dto, Estudiante.class);
        Estudiante savedEstudiante = estudianteRepository.save(estudiante);
        EstudianteResponseDto responseDto = modelMapper.map(savedEstudiante, EstudianteResponseDto.class);

        String recipientEmail = savedEstudiante.getEmail();
        EstudianteCreatedEvent event = new EstudianteCreatedEvent(savedEstudiante, recipientEmail);
        eventPublisher.publishEvent(event);

        return responseDto;
    }

    public EstudianteSelfResponseDto getEstudianteOwnInfo() {
        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null)
            throw new UnauthorizeOperationException("Usuarios anónimos no tienen permiso de acceder a este recurso");

        Estudiante estudiante = estudianteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));
        return modelMapper.map(estudiante, EstudianteSelfResponseDto.class);
    }

    public EstudianteResponseDto getEstudianteInfo(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Estudiante no encontrado"));
        return modelMapper.map(estudiante, EstudianteResponseDto.class);
    }

    public ResponseEntity<String> deleteEstudiante(Long id) {
        // Verificar que el usuario tiene permisos para eliminar
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para eliminar este recurso");
        }

        // Verificar si el estudiante existe
        if (!estudianteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estudiante con ID " + id + " no encontrado");
        }

        // Eliminar el estudiante
        estudianteRepository.deleteById(id);

        // Retornar una respuesta con el mensaje de éxito
        return ResponseEntity.ok("Estudiante con ID " + id + " eliminado con éxito");
    }

    public EstudianteResponseDto updateEstudiante(EstudiantePatchRequestDto estudianteSelfResponseDto) {
        String username = authorizationUtils.getCurrentUserEmail();
        Estudiante estudiante = estudianteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));

        // Solo actualiza los campos que han sido enviados en el DTO
        if (estudianteSelfResponseDto.getFirstName() != null) {
            estudiante.setFirstName(estudianteSelfResponseDto.getFirstName());
        }
        if (estudianteSelfResponseDto.getLastName() != null) {
            estudiante.setLastName(estudianteSelfResponseDto.getLastName());
        }
        if (estudianteSelfResponseDto.getPhoneNumber() != null) {
            estudiante.setPhoneNumber(estudianteSelfResponseDto.getPhoneNumber());
        }
        if (estudianteSelfResponseDto.getEmail() != null) {
            estudiante.setEmail(estudianteSelfResponseDto.getEmail());
        }

        Estudiante updatedEstudiante = estudianteRepository.save(estudiante);

        String recipientEmail = updatedEstudiante.getEmail();
        EstudianteUpdatedEvent event = new EstudianteUpdatedEvent(updatedEstudiante, recipientEmail);
        eventPublisher.publishEvent(event);

        return modelMapper.map(updatedEstudiante, EstudianteResponseDto.class);
    }

}
