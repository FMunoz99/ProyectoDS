package backend.objetoPerdido.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.events.email_event.ObjetoPerdidoCreatedEvent;
import backend.events.email_event.ObjetoPerdidoStatusChangeEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import backend.objetoPerdido.dto.ObjetoPerdidoPatchRequestDto;
import backend.objetoPerdido.dto.ObjetoPerdidoRequestDto;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import backend.objetoPerdido.infrastructure.ObjetoPerdidoRepository;
import backend.usuario.domain.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjetoPerdidoService {

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final UsuarioService usuarioService;
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public ObjetoPerdidoService(ObjetoPerdidoRepository objetoPerdidoRepository,
                                ApplicationEventPublisher publisher,
                                ModelMapper modelMapper, UsuarioService usuarioService,
                                AuthorizationUtils authorizationUtils,
                                EmpleadoRepository empleadoRepository) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.eventPublisher = publisher;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.usuarioService = usuarioService;
        this.empleadoRepository = empleadoRepository;
    }

    public List<ObjetoPerdidoResponseDto> findAllObjetosPerdidos() {
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
        ObjetoPerdido objetoPerdido = modelMapper.map(requestDto, ObjetoPerdido.class);

        objetoPerdido.setEstadoReporte(EstadoReporte.PENDIENTE);
        objetoPerdido.setEstadoTarea(EstadoTarea.NO_FINALIZADO);
        objetoPerdido.setFechaReporte(LocalDate.now());

        ObjetoPerdido savedObjetoPerdido = objetoPerdidoRepository.save(objetoPerdido);

        String studentEmail = savedObjetoPerdido.getEmail();

        // Obtener correos electrónicos de empleados
        List<String> employeeEmails = empleadoRepository.findAllEmpleadosEmails();

        // Crear una lista de correos que incluye al estudiante y a los empleados
        List<String> recipientEmails = new ArrayList<>(employeeEmails);
        recipientEmails.add(studentEmail);

        eventPublisher.publishEvent(new ObjetoPerdidoCreatedEvent(savedObjetoPerdido, recipientEmails));

        return modelMapper.map(savedObjetoPerdido, ObjetoPerdidoResponseDto.class);
    }

    public ObjetoPerdidoResponseDto updateStatusObjetoPerdido(Long id, ObjetoPerdidoPatchRequestDto patchDto) {
        ObjetoPerdido objetoPerdido = objetoPerdidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objeto perdido no encontrado"));

        objetoPerdido.setEstadoReporte(patchDto.getEstadoReporte());
        objetoPerdido.setEstadoTarea(patchDto.getEstadoTarea());
        ObjetoPerdido updatedObjetoPerdido = objetoPerdidoRepository.save(objetoPerdido);

        String recipientEmail = updatedObjetoPerdido.getEmail();
        eventPublisher.publishEvent(new ObjetoPerdidoStatusChangeEvent(updatedObjetoPerdido, recipientEmail));
        return modelMapper.map(updatedObjetoPerdido, ObjetoPerdidoResponseDto.class);
    }


    public void deleteObjetoPerdido(Long id) {
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
}
