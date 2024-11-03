package backend.empleado.domain;

import backend.auth.utils.AuthorizationUtils;
import backend.empleado.dto.EmpleadoPatchRequestDto;
import backend.empleado.dto.EmpleadoResponseDto;
import backend.empleado.infrastructure.EmpleadoRepository;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService {

    final private EmpleadoRepository empleadoRepository;
    final private AuthorizationUtils authorizationUtils;
    final private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository, AuthorizationUtils authorizationUtils) {
        this.empleadoRepository = empleadoRepository;
        this.authorizationUtils = authorizationUtils;
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

    public EmpleadoResponseDto getEmpleadoOwnInfo() {
        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null)
            throw new UnauthorizeOperationException("Usuarios anónimos no están autorizados para acceder a este recurso");

        Empleado empleado = empleadoRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Empleado no encontrado"));
        return getEmpleadoInfo(empleado.getId());
    }

    public void deleteEmpleado (Long id) {
        if (!authorizationUtils.isAdminOrResourceOwner(id))
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");

        empleadoRepository.deleteById(id);
    }

    public EmpleadoResponseDto updateEmpleadoInfo (Long id, EmpleadoPatchRequestDto empleadoInfo) {
        if (!authorizationUtils.isAdminOrResourceOwner(id))
            throw new UnauthorizeOperationException("El usuario no tiene permiso para modificar este recurso");

        Empleado empleado = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        empleado.setFirstName(empleadoInfo.getFirstName());
        empleado.setLastName(empleadoInfo.getLastName());
        empleado.setPhoneNumber(empleadoInfo.getPhoneNumber());
        empleado.setHorarioDeTrabajo(empleadoInfo.getHorarioDeTrabajo());

        empleadoRepository.save(empleado);

        return modelMapper.map(empleado, EmpleadoResponseDto.class);
    }
}
