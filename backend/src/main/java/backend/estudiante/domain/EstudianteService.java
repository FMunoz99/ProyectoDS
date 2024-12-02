package backend.estudiante.domain;

import backend.auth.exceptions.UserAlreadyExistException;
import backend.auth.utils.AuthorizationUtils;
import backend.aws_s3.StorageService;
import backend.estudiante.dto.EstudiantePatchRequestDto;
import backend.estudiante.dto.EstudianteRequestDto;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.dto.EstudianteSelfResponseDto;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.events.email_event.EstudianteCreatedEvent;
import backend.events.email_event.EstudianteUpdatedEvent;
import backend.exceptions.ResourceNotFoundException;
import backend.usuario.domain.Role;
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

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final StorageService storageService;

    @Autowired
    public EstudianteService(EstudianteRepository estudianteRepository, ModelMapper modelMapper,
                             AuthorizationUtils authorizationUtils, ApplicationEventPublisher eventPublisher,
                             PasswordEncoder passwordEncoder, StorageService storageService) {
        this.estudianteRepository = estudianteRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authorizationUtils = authorizationUtils;
        this.eventPublisher = eventPublisher;
        this.storageService = storageService;
    }

    public List<EstudianteResponseDto> getAllEstudiantes() {
        if (!authorizationUtils.isAdminOrEmpleado()) {
            throw new UnauthorizeOperationException("Solo los administradores o empleados pueden ver la lista de estudiantes");
        }

        List<Estudiante> estudiantes = estudianteRepository.findAll();

        return estudiantes.stream().map(estudiante -> {
            EstudianteResponseDto estudianteDto = modelMapper.map(estudiante, EstudianteResponseDto.class);

            // Generar URL pre-firmada para la foto de perfil, si existe
            if (estudiante.getFotoPerfilUrl() != null) {
                estudianteDto.setFotoPerfilUrl(storageService.generatePresignedUrl(estudiante.getFotoPerfilUrl()));
            }

            return estudianteDto;
        }).toList();
    }

    public EstudianteResponseDto createEstudiante(EstudianteRequestDto dto) {

        // Verificar si el usuario tiene el rol de administrador
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden crear estudiantes");
        }

        // Comprobación si el estudiante con el mismo email ya existe
        if (estudianteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Estudiante con email " + dto.getEmail() + " ya existe.");
        }

        // Convertir el EstudianteRequestDto a Estudiante
        Estudiante estudiante = modelMapper.map(dto, Estudiante.class);

        // Encriptar la contraseña (si es necesario) y asignar otros datos básicos
        estudiante.setPassword(passwordEncoder.encode(dto.getPassword()));  // Asignamos la contraseña encriptada
        estudiante.setRole(Role.ESTUDIANTE);  // Asignamos el rol de estudiante
        estudiante.setPhoneNumber(dto.getPhoneNumber());  // Asignamos el número de teléfono
        estudiante.setUpdatedAt(ZonedDateTime.now());  // Establecemos la fecha de actualización
        estudiante.setCreatedAt(ZonedDateTime.now());  // Establecemos la fecha de creación

        // Guardar el estudiante en la base de datos
        Estudiante savedEstudiante = estudianteRepository.save(estudiante);

        // Publicar el evento de creación del estudiante
        String recipientEmail = savedEstudiante.getEmail();
        EstudianteCreatedEvent event = new EstudianteCreatedEvent(savedEstudiante, recipientEmail);
        eventPublisher.publishEvent(event);

        // Convertir el estudiante guardado a DTO y retornarlo
        return modelMapper.map(savedEstudiante, EstudianteResponseDto.class);
    }


    public EstudianteSelfResponseDto getEstudianteOwnInfo() {
        // Verificar si el usuario autenticado tiene el rol de estudiante
        if (!authorizationUtils.isEstudiante()) {
            throw new UnauthorizeOperationException("Solo el estudiante autenticado puede acceder a este recurso");
        }

        // Obtener el email del usuario autenticado
        String username = authorizationUtils.getCurrentUserEmail();
        Estudiante estudiante = estudianteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));

        // Mapear a DTO
        EstudianteSelfResponseDto estudianteDto = modelMapper.map(estudiante, EstudianteSelfResponseDto.class);

        // Generar URL pre-firmada para la foto de perfil, si la URL no está vacía
        if (estudiante.getFotoPerfilUrl() != null && !estudiante.getFotoPerfilUrl().isEmpty()) {
            // Extraer la clave del objeto si es una URL completa
            String fotoPerfilKey = estudiante.getFotoPerfilUrl();
            if (fotoPerfilKey.startsWith("https://")) {
                fotoPerfilKey = estudiante.getFotoPerfilUrl().replace("https://ds-proy-bucket.s3.amazonaws.com/", "");
            }
            // Intentar generar la URL pre-firmada
            String presignedUrl = storageService.generatePresignedUrl(fotoPerfilKey);
            estudianteDto.setFotoPerfilUrl(presignedUrl);
        }

        return estudianteDto;
    }




    public EstudianteResponseDto getEstudianteInfo(Long id) {
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("El usuario no tiene permiso para acceder a este recurso");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        EstudianteResponseDto estudianteDto = modelMapper.map(estudiante, EstudianteResponseDto.class);

        // Generar URL pre-firmada para la foto de perfil, si existe
        if (estudiante.getFotoPerfilUrl() != null) {
            estudianteDto.setFotoPerfilUrl(storageService.generatePresignedUrl(estudiante.getFotoPerfilUrl()));
        }

        return estudianteDto;
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

    public EstudianteResponseDto updateEstudiante(EstudiantePatchRequestDto patchEstudianteDto, MultipartFile fotoPerfil) throws IOException {
        String username = authorizationUtils.getCurrentUserEmail();
        Estudiante estudiante = estudianteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));

        // Map para registrar los campos que se actualizan
        Map<String, String> updatedFields = new HashMap<>();

        // Comparar y actualizar solo los campos proporcionados
        if (patchEstudianteDto.getFirstName() != null && !patchEstudianteDto.getFirstName().equals(estudiante.getFirstName())) {
            updatedFields.put("Nombre", patchEstudianteDto.getFirstName());
            estudiante.setFirstName(patchEstudianteDto.getFirstName());
        }

        if (patchEstudianteDto.getLastName() != null && !patchEstudianteDto.getLastName().equals(estudiante.getLastName())) {
            updatedFields.put("Apellido", patchEstudianteDto.getLastName());
            estudiante.setLastName(patchEstudianteDto.getLastName());
        }

        if (patchEstudianteDto.getPhoneNumber() != null && !patchEstudianteDto.getPhoneNumber().equals(estudiante.getPhoneNumber())) {
            updatedFields.put("Teléfono", patchEstudianteDto.getPhoneNumber());
            estudiante.setPhoneNumber(patchEstudianteDto.getPhoneNumber());
        }

        if (patchEstudianteDto.getEmail() != null && !patchEstudianteDto.getEmail().equals(estudiante.getEmail())) {
            updatedFields.put("Email", patchEstudianteDto.getEmail());
            estudiante.setEmail(patchEstudianteDto.getEmail());
        }

        if (patchEstudianteDto.getPassword() != null) {
            // Validar si la nueva contraseña es diferente antes de actualizar
            if (!passwordEncoder.matches(patchEstudianteDto.getPassword(), estudiante.getPassword())) {
                updatedFields.put("Contraseña", "Actualizada");
                estudiante.setPassword(passwordEncoder.encode(patchEstudianteDto.getPassword()));
            }
        }

        // Subir y actualizar la foto de perfil si se proporciona
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String fotoUrl = storageService.uploadFile(fotoPerfil, "estudiantes/" + estudiante.getEmail());
            updatedFields.put("Foto de Perfil", "Actualizada");
            estudiante.setFotoPerfilUrl(fotoUrl);
        }

        // Actualizar la fecha de modificación solo si hay cambios
        if (!updatedFields.isEmpty()) {
            estudiante.setUpdatedAt(ZonedDateTime.now());
            Estudiante updatedEstudiante = estudianteRepository.save(estudiante);

            // Enviar evento
            String recipientEmail = updatedEstudiante.getEmail();
            EstudianteUpdatedEvent event = new EstudianteUpdatedEvent(updatedEstudiante, updatedFields, recipientEmail);
            eventPublisher.publishEvent(event);

            return modelMapper.map(updatedEstudiante, EstudianteResponseDto.class);
        }

        // Retornar el estudiante sin cambios si no hubo actualizaciones
        return modelMapper.map(estudiante, EstudianteResponseDto.class);
    }


}
