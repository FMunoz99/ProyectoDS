package backend.incidente.domain;

import backend.admin.component.ReportCounter;
import backend.auth.utils.AuthorizationUtils;
import backend.aws_s3.StorageService;
import backend.empleado.domain.Empleado;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.domain.EstudianteService;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.events.email_event.IncidenteCreatedEmpleadoEvent;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final EstudianteService estudianteService;
    private final ReportCounter reportCounter;
    private final StorageService storageService;

    @Autowired
    public IncidenteService(IncidenteRepository incidenteRepository, ApplicationEventPublisher publisher,
                            ModelMapper modelMapper, UsuarioService usuarioService, ReportCounter reportCounter,
                            AuthorizationUtils authorizationUtils, EmpleadoRepository empleadoRepository,
                            EstudianteRepository estudianteRepository, EstudianteService estudianteService,
                            StorageService storageService) {
        this.incidenteRepository = incidenteRepository;
        this.eventPublisher = publisher;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.usuarioService = usuarioService;
        this.empleadoRepository = empleadoRepository;
        this.estudianteRepository = estudianteRepository;
        this.estudianteService = estudianteService;
        this.reportCounter = reportCounter;
        this.storageService = storageService;
    }

    public List<IncidenteResponseDto> findAllIncidentes() {
        if (!authorizationUtils.isAdminOrEmpleado()) {
            throw new UnauthorizeOperationException("Solo los administradores y empleados pueden ver todos los reportes de incidentes");
        }

        return incidenteRepository.findAll().stream()
                .map(incidente -> {
                    IncidenteResponseDto dto = modelMapper.map(incidente, IncidenteResponseDto.class);
                    if (incidente.getFotoIncidenteUrl() != null && !incidente.getFotoIncidenteUrl().isEmpty()) {
                        dto.setFotoIncidenteUrl(storageService.generatePresignedUrl(incidente.getFotoIncidenteUrl()));
                    }
                    return dto;
                })
                .toList();
    }

    public IncidenteResponseDto findIncidenteById(Long id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado"));

        IncidenteResponseDto dto = modelMapper.map(incidente, IncidenteResponseDto.class);
        if (incidente.getFotoIncidenteUrl() != null && !incidente.getFotoIncidenteUrl().isEmpty()) {
            dto.setFotoIncidenteUrl(storageService.generatePresignedUrl(incidente.getFotoIncidenteUrl()));
        }
        return dto;
    }

    public IncidenteResponseDto saveIncidente(IncidenteRequestDto requestDto, MultipartFile fotoIncidente) throws IOException {

        // Obtener el correo del usuario autenticado
        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null)
            throw new UnauthorizeOperationException("Usuario anónimo no tiene permitido acceder a este recurso");

        // Buscar al estudiante que está realizando la solicitud usando el email del usuario autenticado
        Optional<Estudiante> optionalEstudianteRegistrador = estudianteRepository.findByEmail(username);
        if (optionalEstudianteRegistrador.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró un estudiante asociado al usuario autenticado");
        }
        Estudiante estudianteRegistrador = optionalEstudianteRegistrador.get();

        // Mapeo del DTO a la entidad Incidente
        Incidente incidente = modelMapper.map(requestDto, Incidente.class);

        // Seteo de otros campos
        incidente.setPiso(requestDto.getPiso());
        incidente.setDetalle(requestDto.getDetalle());
        incidente.setUbicacion(requestDto.getUbicacion());
        incidente.setEmail(requestDto.getEmail());
        incidente.setPhoneNumber(requestDto.getPhoneNumber());
        incidente.setDescription(requestDto.getDescription());
        incidente.setEstadoReporte(EstadoReporte.PENDIENTE);
        incidente.setEstadoTarea(EstadoTarea.NO_FINALIZADO);

        // Setear la fecha de reporte automáticamente si no está presente
        if (requestDto.getFechaReporte() == null) {
            incidente.setFechaReporte(LocalDate.now());
        } else {
            incidente.setFechaReporte(requestDto.getFechaReporte());
        }

        // Asignar el estudiante que registró el incidente
        incidente.setEstudiante(estudianteRegistrador);

        // Manejo de la imagen si se proporciona
        if (fotoIncidente != null && !fotoIncidente.isEmpty()) {
            String imagenUrl = storageService.uploadFile(fotoIncidente, "incidentes/" + estudianteRegistrador.getEmail());
            incidente.setFotoIncidenteUrl(imagenUrl); // Supongamos que el campo `imagenUrl` existe en la entidad `Incidente`
        }

        // Buscar un empleado disponible al azar
        List<Empleado> empleados = empleadoRepository.findAll();
        String empleadoEmail = null;
        if (!empleados.isEmpty()) {
            Random random = new Random();
            Empleado empleado = empleados.get(random.nextInt(empleados.size()));
            incidente.setEmpleado(empleado);  // Asignar empleado al incidente
            empleadoEmail = empleado.getEmail();  // Obtener el correo del empleado seleccionado
        }

        // Incrementar el contador
        String fechaReporte = incidente.getFechaReporte().toString();
        reportCounter.incrementarIncidentes(fechaReporte);

        // Guardar el incidente en la base de datos
        Incidente savedIncidente = incidenteRepository.save(incidente);

        // Publicar el evento para notificar solo al estudiante que lo registró
        eventPublisher.publishEvent(new IncidenteCreatedEvent(savedIncidente, estudianteRegistrador.getEmail()));

        // Solo si se asignó un empleado, publicar el evento para notificar al empleado
        if (empleadoEmail != null) {
            eventPublisher.publishEvent(new IncidenteCreatedEmpleadoEvent(savedIncidente, empleadoEmail));
        }

        // Mapear y devolver el DTO de respuesta
        return modelMapper.map(savedIncidente, IncidenteResponseDto.class);
    }

    public IncidenteResponseDto updateStatusIncidente(Long id, IncidentePatchRequestDto patchDto) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado"));

        if (!authorizationUtils.isAdminOrEmpleado(incidente.getEmpleado())) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        }

        // Actualiza solo los campos que no son null en patchDto
        if (patchDto.getEstadoReporte() != null) {
            incidente.setEstadoReporte(patchDto.getEstadoReporte());
        }
        if (patchDto.getEstadoTarea() != null) {
            incidente.setEstadoTarea(patchDto.getEstadoTarea());
        }

        Incidente updatedIncidente = incidenteRepository.save(incidente);

        eventPublisher.publishEvent(new IncidenteStatusChangeEvent(updatedIncidente, updatedIncidente.getEmail()));
        return modelMapper.map(updatedIncidente, IncidenteResponseDto.class);
    }


    public void deleteIncidente(Long id) {
        // Verificar si el incidente existe
        if (!incidenteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incidente no encontrado");
        }

        // Verificar si el usuario tiene el rol de administrador
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden eliminar este recurso");
        }

        incidenteRepository.deleteById(id);
    }

    public List<IncidenteResponseDto> getIncidentesByEstudiante() {
        Estudiante estudiante = usuarioService.getAuthenticatedEstudiante();
        List<Incidente> incidentes = incidenteRepository.findByEstudiante(estudiante);

        return incidentes.stream()
                .map(incidente -> {
                    IncidenteResponseDto dto = modelMapper.map(incidente, IncidenteResponseDto.class);
                    if (incidente.getFotoIncidenteUrl() != null && !incidente.getFotoIncidenteUrl().isEmpty()) {
                        dto.setFotoIncidenteUrl(storageService.generatePresignedUrl(incidente.getFotoIncidenteUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // NUEVOS MÉTODOS

    // Obtener incidentes por estado
    public List<IncidenteResponseDto> getIncidentesPorEstado(EstadoReporte estadoReporte) {
        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden acceder a este recurso.");
        }

        List<Incidente> incidentes = incidenteRepository.findByEstadoReporte(estadoReporte);

        return incidentes.stream()
                .map(incidente -> {
                    IncidenteResponseDto dto = modelMapper.map(incidente, IncidenteResponseDto.class);
                    if (incidente.getFotoIncidenteUrl() != null && !incidente.getFotoIncidenteUrl().isEmpty()) {
                        dto.setFotoIncidenteUrl(storageService.generatePresignedUrl(incidente.getFotoIncidenteUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener incidentes por estado de tarea (FINALIZADO, NO_FINALIZADO)
    public List<IncidenteResponseDto> getIncidentesPorEstadoTarea(EstadoTarea estadoTarea) {
        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden acceder a este recurso.");
        }

        List<Incidente> incidentes = incidenteRepository.findByEstadoTarea(estadoTarea);

        return incidentes.stream()
                .map(incidente -> {
                    IncidenteResponseDto dto = modelMapper.map(incidente, IncidenteResponseDto.class);
                    if (incidente.getFotoIncidenteUrl() != null && !incidente.getFotoIncidenteUrl().isEmpty()) {
                        dto.setFotoIncidenteUrl(storageService.generatePresignedUrl(incidente.getFotoIncidenteUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener incidentes por ID de estudiante
    public List<IncidenteResponseDto> getIncidentesPorEstudiante(Long estudianteId) {
        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden acceder a este recurso.");
        }

        EstudianteResponseDto estudianteDto = estudianteService.getEstudianteInfo(estudianteId);

        List<Incidente> incidentes = incidenteRepository.findByEstudianteId(estudianteDto.getId());

        return incidentes.stream()
                .map(incidente -> {
                    IncidenteResponseDto dto = modelMapper.map(incidente, IncidenteResponseDto.class);
                    if (incidente.getFotoIncidenteUrl() != null && !incidente.getFotoIncidenteUrl().isEmpty()) {
                        dto.setFotoIncidenteUrl(storageService.generatePresignedUrl(incidente.getFotoIncidenteUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
