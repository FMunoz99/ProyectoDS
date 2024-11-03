package backend.incidente.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.IncidenteCreatedEvent;
import backend.events.email_event.IncidenteStatusChangeEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.incidente.dto.IncidentePatchRequestDto;
import backend.incidente.dto.IncidenteResponseDto;
import backend.incidente.infrastructure.IncidenteRepository;
import backend.usuario.domain.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final UsuarioService usuarioService;
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public IncidenteService(IncidenteRepository incidenteRepository,
                            ApplicationEventPublisher publisher,
                            ModelMapper modelMapper, UsuarioService usuarioService ,
                            AuthorizationUtils authorizationUtils, EmpleadoRepository empleadoRepository) {
        this.incidenteRepository = incidenteRepository;
        this.eventPublisher = publisher;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.usuarioService = usuarioService;
        this.empleadoRepository = empleadoRepository;
    }

    public List<IncidenteResponseDto> findAllIncidentes() {
        return incidenteRepository.findAll().stream()
                .map(incidente -> modelMapper.map(incidente, IncidenteResponseDto.class))
                .toList();
    }

    public IncidenteResponseDto findIncidenteById(Long id) {
        return incidenteRepository.findById(id)
                .map(incidente -> modelMapper.map(incidente, IncidenteResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado"));
    }

    public IncidenteResponseDto saveIncidente(Incidente incidente) {
        Incidente savedIncidente = incidenteRepository.save(incidente);

        String studentEmail = savedIncidente.getEmail();

        List<String> employeeEmails = empleadoRepository.findAllEmpleadosEmails();

        // Crear una lista de correos que incluye al estudiante y a los empleados
        List<String> recipientEmails = new ArrayList<>(employeeEmails);
        recipientEmails.add(studentEmail);

        // Publicar el evento para notificar a todos los destinatarios
        eventPublisher.publishEvent(new IncidenteCreatedEvent(savedIncidente, recipientEmails));

        return modelMapper.map(savedIncidente, IncidenteResponseDto.class);
    }


    public IncidenteResponseDto updateStatusIncidente(Long id, IncidentePatchRequestDto patchDto) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado"));

        if (!authorizationUtils.isAdminOrResourceOwner(incidente.getEstudiante(), incidente.getEmpleado())) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        }

        incidente.setEstadoReporte(patchDto.getEstadoReporte());
        incidente.setEstadoTarea(patchDto.getEstadoTarea());
        Incidente updatedIncidente = incidenteRepository.save(incidente);

        eventPublisher.publishEvent(new IncidenteStatusChangeEvent(updatedIncidente, updatedIncidente.getEmail()));
        return modelMapper.map(updatedIncidente, IncidenteResponseDto.class);
    }

    public void deleteIncidente(Long id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado"));

        if (!authorizationUtils.isAdminOrResourceOwner(incidente.getEstudiante(), incidente.getEmpleado())) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para eliminar este recurso");
        }

        incidenteRepository.deleteById(id);
    }

    public List<IncidenteResponseDto> getIncidentesByEstudiante() {
        Estudiante estudiante = usuarioService.getAuthenticatedEstudiante();
        List<Incidente> incidentes = incidenteRepository.findByEstudiante(estudiante);
        return incidentes.stream()
                .map(incidente -> modelMapper.map(incidente, IncidenteResponseDto.class))
                .collect(Collectors.toList());
    }
}
