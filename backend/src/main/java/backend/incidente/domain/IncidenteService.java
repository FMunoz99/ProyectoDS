package backend.incidente.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.IncidenteCreatedEvent;
import backend.events.email_event.IncidenteStatusChangeEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.incidente.dto.IncidentePatchRequestDto;
import backend.incidente.dto.IncidenteResponseDto;
import backend.incidente.infrastructure.IncidenteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;

    @Autowired
    public IncidenteService(IncidenteRepository incidenteRepository,
                            ApplicationEventPublisher publisher,
                            ModelMapper modelMapper,
                            AuthorizationUtils authorizationUtils) {
        this.incidenteRepository = incidenteRepository;
        this.eventPublisher = publisher;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
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
        eventPublisher.publishEvent(new IncidenteCreatedEvent(savedIncidente, savedIncidente.getEmail()));
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
}
