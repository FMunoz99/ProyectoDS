package backend.incidente.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.domain.Empleado;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.events.email_event.IncidenteCreatedEvent;
import backend.events.email_event.IncidenteStatusChangeEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.incidente.dto.IncidentePatchRequestDto;
import backend.incidente.dto.IncidenteRequestDto;
import backend.incidente.dto.IncidenteResponseDto;
import backend.incidente.infrastructure.IncidenteRepository;
import backend.usuario.domain.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final UsuarioService usuarioService;
    private final EmpleadoRepository empleadoRepository;
    private final EstudianteRepository estudianteRepository;

    @Autowired
    public IncidenteService(IncidenteRepository incidenteRepository,
                            ApplicationEventPublisher publisher,
                            ModelMapper modelMapper, UsuarioService usuarioService ,
                            AuthorizationUtils authorizationUtils, EmpleadoRepository empleadoRepository,
                            EstudianteRepository estudianteRepository) {
        this.incidenteRepository = incidenteRepository;
        this.eventPublisher = publisher;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.usuarioService = usuarioService;
        this.empleadoRepository = empleadoRepository;
        this.estudianteRepository = estudianteRepository;
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

    public IncidenteResponseDto saveIncidente(IncidenteRequestDto requestDto) {
        // Mapeo del DTO a la entidad Incidente
        Incidente incidente = modelMapper.map(requestDto, Incidente.class);

        // Seteo de otros campos
        incidente.setPiso(requestDto.getPiso());
        incidente.setDetalle(requestDto.getDetalle());
        incidente.setUbicacion(requestDto.getUbicacion());
        incidente.setEmail(requestDto.getEmail());
        incidente.setPhoneNumber(requestDto.getPhoneNumber());
        incidente.setDescripcion(requestDto.getDescripcion());
        incidente.setEstadoReporte(EstadoReporte.PENDIENTE);
        incidente.setEstadoTarea(EstadoTarea.NO_FINALIZADO);
        incidente.setFechaReporte(requestDto.getFechaReporte());

        // Obtener el correo del estudiante
        String studentEmail = incidente.getEmail();

        // Buscar al estudiante en la base de datos usando su email
        Optional<Estudiante> optionalEstudiante = estudianteRepository.findByEmail(studentEmail);
        if (optionalEstudiante.isPresent()) {
            Estudiante estudiante = optionalEstudiante.get();
            // Asignar la entidad Estudiante al incidente y su ID
            incidente.setEstudiante(estudiante);
            incidente.setEstudianteId(estudiante.getId());  // Asignar el ID del estudiante
        } else {
            incidente.setEstudiante(null);
            incidente.setEstudianteId(null);
        }

        // Buscar un empleado disponible al azar
        List<Empleado> empleados = empleadoRepository.findAll();  // Obtener todos los empleados
        if (!empleados.isEmpty()) {
            // Seleccionar un empleado aleatorio
            Random random = new Random();
            Empleado empleado = empleados.get(random.nextInt(empleados.size()));
            // Asignar el empleado al incidente y su ID
            incidente.setEmpleado(empleado);
            incidente.setEmpleadoId(empleado.getId());  // Asignar el ID del empleado
        } else {
            incidente.setEmpleado(null);
            incidente.setEmpleadoId(null);  // En caso de que no haya empleados disponibles
        }

        // Guardar el incidente en la base de datos
        Incidente savedIncidente = incidenteRepository.save(incidente);

        // Obtener los correos de los empleados
        List<String> employeeEmails = empleadoRepository.findAllEmpleadosEmails();

        // Crear una lista de correos que incluye al estudiante y a los empleados
        List<String> recipientEmails = new ArrayList<>(employeeEmails);
        recipientEmails.add(studentEmail);  // También agregamos el correo del estudiante

        // Publicar el evento para notificar a los destinatarios
        eventPublisher.publishEvent(new IncidenteCreatedEvent(savedIncidente, recipientEmails));

        // Mapear y devolver el DTO de respuesta
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
