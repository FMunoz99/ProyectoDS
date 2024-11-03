package backend.estudiante.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.estudiante.dto.EstudiantePatchRequestDto;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.dto.EstudianteSelfResponseDto;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.estudiante.infrastructure.EstudianteRepository;
import backend.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;

    @Autowired
    public EstudianteService(EstudianteRepository estudianteRepository, ModelMapper modelMapper,
                             AuthorizationUtils authorizationUtils) {
        this.estudianteRepository = estudianteRepository;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
    }

    public EstudianteSelfResponseDto getEstudianteOwnInfo() {
        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null)
            throw new UnauthorizeOperationException("Usuarios anónimos no tienen permiso de acceder a este recurso");

        Estudiante estudiante = estudianteRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));
        return modelMapper.map(estudiante, EstudianteSelfResponseDto.class);
    }

    public EstudianteResponseDto getEstudianteInfo(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Estudiante no encontrado"));
        return modelMapper.map(estudiante, EstudianteResponseDto.class);
    }

    public void deleteEstudiante(Long id) {
        if (!authorizationUtils.isAdminOrResourceOwner(id))
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");
        estudianteRepository.deleteById(id);
    }

    public EstudianteResponseDto updateEstudiante (EstudiantePatchRequestDto estudianteSelfResponseDto) {
        String username = authorizationUtils.getCurrentUserEmail();
        Estudiante estudiante = estudianteRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Estudiante no encontrado"));

        estudiante.setFirstName(estudianteSelfResponseDto.getFirstName());
        estudiante.setLastName(estudianteSelfResponseDto.getLastName());
        estudiante.setPhoneNumber(estudianteSelfResponseDto.getPhoneNumber());

        estudianteRepository.save(estudiante);

        return modelMapper.map(estudiante, EstudianteResponseDto.class);
    }
}
