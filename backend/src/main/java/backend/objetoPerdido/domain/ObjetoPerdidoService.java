package backend.objetoPerdido.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.domain.Empleado;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.domain.EstudianteService;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.events.email_event.ObjetoPerdidoCreatedEmpleadoEvent;
import backend.events.email_event.ObjetoPerdidoCreatedEvent;
import backend.events.email_event.ObjetoPerdidoStatusChangeEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import backend.incidente.domain.Incidente;
import backend.objetoPerdido.dto.ObjetoPerdidoPatchRequestDto;
import backend.objetoPerdido.dto.ObjetoPerdidoRequestDto;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import backend.objetoPerdido.infrastructure.ObjetoPerdidoRepository;
import backend.usuario.domain.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ObjetoPerdidoService {

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final UsuarioService usuarioService;
    private final EmpleadoRepository empleadoRepository;
    private final EstudianteRepository estudianteRepository;
    private final EstudianteService estudianteService;

    @Autowired
    public ObjetoPerdidoService(ObjetoPerdidoRepository objetoPerdidoRepository,
                                ApplicationEventPublisher publisher,
                                ModelMapper modelMapper, UsuarioService usuarioService,
                                AuthorizationUtils authorizationUtils, EstudianteService estudianteService ,
                                EmpleadoRepository empleadoRepository, EstudianteRepository estudianteRepository) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.eventPublisher = publisher;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.usuarioService = usuarioService;
        this.empleadoRepository = empleadoRepository;
        this.estudianteRepository = estudianteRepository;
        this.estudianteService = estudianteService;
    }

    public List<ObjetoPerdidoResponseDto> findAllObjetosPerdidos() {

        // Verificar si el usuario autenticado es un administrador o un empleado
        if (!authorizationUtils.isAdminOrEmpleado()) {
            throw new UnauthorizeOperationException("Solo los administradores y empleados pueden ver todos los reportes de objetos perdidos");
        }
        return objetoPerdidoRepository.findAll().stream()
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .toList();
    }

    public ObjetoPerdidoResponseDto findObjetoPerdidoById(Long id) {
        return objetoPerdidoRepository.findById(id)
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Objeto perdido no encontrado"));
    }

    public ObjetoPerdidoResponseDto saveObjetoPerdido(ObjetoPerdidoRequestDto requestDto) {

        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null)
            throw new UnauthorizeOperationException("Usuario anónimo no tiene permitido acceder a este recurso");

        // Buscar al estudiante que está realizando la solicitud usando el email del usuario autenticado
        Optional<Estudiante> optionalEstudianteRegistrador = estudianteRepository.findByEmail(username);
        if (optionalEstudianteRegistrador.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró un estudiante asociado al usuario autenticado");
        }
        Estudiante estudianteRegistrador = optionalEstudianteRegistrador.get();

        // Mapeo del DTO a la entidad ObjetoPerdido
        ObjetoPerdido objetoPerdido = modelMapper.map(requestDto, ObjetoPerdido.class);

        // Seteo de otros campos
        objetoPerdido.setPiso(requestDto.getPiso());
        objetoPerdido.setDetalle(requestDto.getDetalle());
        objetoPerdido.setUbicacion(requestDto.getUbicacion());
        objetoPerdido.setEmail(requestDto.getEmail());
        objetoPerdido.setPhoneNumber(requestDto.getPhoneNumber());
        objetoPerdido.setDescription(requestDto.getDescription());
        objetoPerdido.setEstadoReporte(EstadoReporte.PENDIENTE);
        objetoPerdido.setEstadoTarea(EstadoTarea.NO_FINALIZADO);
        objetoPerdido.setFechaReporte(requestDto.getFechaReporte());

        // Asignar el estudiante que registró el objeto perdido
        objetoPerdido.setEstudiante(estudianteRegistrador);

        // Buscar un empleado disponible al azar
        List<Empleado> empleados = empleadoRepository.findAll();
        String empleadoEmail = null;
        if (!empleados.isEmpty()) {
            // Seleccionar un empleado aleatorio
            Random random = new Random();
            Empleado empleado = empleados.get(random.nextInt(empleados.size()));
            objetoPerdido.setEmpleado(empleado);  // Asignar empleado al objeto perdido
            empleadoEmail = empleado.getEmail();  // Obtener el correo del empleado seleccionado
        }

        // Guardar el objeto perdido en la base de datos
        ObjetoPerdido savedObjetoPerdido = objetoPerdidoRepository.save(objetoPerdido);

        // Publicar el evento para notificar solo al estudiante que lo registró
        eventPublisher.publishEvent(new ObjetoPerdidoCreatedEvent(savedObjetoPerdido, estudianteRegistrador.getEmail()));

        // Solo si se asignó un empleado, publicar el evento para notificar al empleado
        if (empleadoEmail != null) {
            eventPublisher.publishEvent(new ObjetoPerdidoCreatedEmpleadoEvent(savedObjetoPerdido, empleadoEmail));
        }

        // Mapear y devolver el DTO de respuesta
        return modelMapper.map(savedObjetoPerdido, ObjetoPerdidoResponseDto.class);
    }

    public ObjetoPerdidoResponseDto updateStatusObjetoPerdido(Long id, ObjetoPerdidoPatchRequestDto patchDto) {
        ObjetoPerdido objetoPerdido = objetoPerdidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objeto perdido no encontrado"));

        if (!authorizationUtils.isAdminOrEmpleado(objetoPerdido.getEmpleado())) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        }

        // Actualiza solo los campos que no son null en patchDto
        if (patchDto.getEstadoReporte() != null) {
            objetoPerdido.setEstadoReporte(patchDto.getEstadoReporte());
        }
        if (patchDto.getEstadoTarea() != null) {
            objetoPerdido.setEstadoTarea(patchDto.getEstadoTarea());
        }

        ObjetoPerdido updatedObjetoPerdido = objetoPerdidoRepository.save(objetoPerdido);

        String recipientEmail = updatedObjetoPerdido.getEmail();
        eventPublisher.publishEvent(new ObjetoPerdidoStatusChangeEvent(updatedObjetoPerdido, recipientEmail));
        return modelMapper.map(updatedObjetoPerdido, ObjetoPerdidoResponseDto.class);
    }


    public void deleteObjetoPerdido(Long id) {
        ObjetoPerdido objetoPerdido = objetoPerdidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objeto Perdido no encontrado"));

        if (!authorizationUtils.isAdminOrResourceOwner(id))
            throw new UnauthorizeOperationException("El usuario no tiene permiso para eliminar este recurso");
        objetoPerdidoRepository.deleteById(id);
    }

    public List<ObjetoPerdidoResponseDto> getObjetosPerdidosByEstudiante() {
        Estudiante estudiante = usuarioService.getAuthenticatedEstudiante();
        List<ObjetoPerdido> objetosPerdidos = objetoPerdidoRepository.findByEstudiante(estudiante);
        return objetosPerdidos.stream()
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .collect(Collectors.toList());
    }

    // NUEVOS MÉTODOS

    // Obtener objetos perdidos por estado
    public List<ObjetoPerdidoResponseDto> getObjetosPerdidosPorEstado(EstadoReporte estadoReporte) {

        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden acceder a este recurso.");
        }

        List<ObjetoPerdido> objetosPerdidos = objetoPerdidoRepository.findByEstadoReporte(estadoReporte);
        return objetosPerdidos.stream()
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .collect(Collectors.toList());
    }

    // Obtener objetos perdidos por estado de tarea (FINALIZADO, NO_FINALIZADO)
    public List<ObjetoPerdidoResponseDto> getObjetosPerdidosPorEstadoTarea(EstadoTarea estadoTarea) {

        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden acceder a este recurso.");
        }

        List<ObjetoPerdido> objetosPerdidos = objetoPerdidoRepository.findByEstadoTarea(estadoTarea);
        return objetosPerdidos.stream()
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .collect(Collectors.toList());
    }

    // Obtener objetos perdidos por ID de estudiante
    public List<ObjetoPerdidoResponseDto> getObjetosPerdidosPorEstudiante(Long estudianteId) {

        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden acceder a este recurso.");
        }

        // Obtener el EstudianteResponseDto desde el servicio
        EstudianteResponseDto estudianteDto = estudianteService.getEstudianteInfo(estudianteId);

        // Usar el ID del EstudianteResponseDto para buscar los objetos perdidos relacionados con el estudiante
        List<ObjetoPerdido> objetosPerdidos = objetoPerdidoRepository.findByEstudianteId(estudianteDto.getId());

        // Mapear los objetos perdidos a DTOs
        return objetosPerdidos.stream()
                .map(objetoPerdido -> modelMapper.map(objetoPerdido, ObjetoPerdidoResponseDto.class))
                .collect(Collectors.toList());
    }

}
