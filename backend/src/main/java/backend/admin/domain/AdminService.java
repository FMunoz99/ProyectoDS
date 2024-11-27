package backend.admin.domain;

import backend.admin.dto.AdminPatchRequestDto;
import backend.admin.dto.AdminRequestDto;
import backend.admin.dto.AdminResponseDto;
import backend.admin.dto.AdminSelfResponseDto;
import backend.admin.infrastructure.AdminRepository;
import backend.usuario.domain.Role;
import backend.exceptions.ResourceNotFoundException;
import backend.auth.utils.AuthorizationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    final private AdminRepository adminRepository;
    final private AuthorizationUtils authorizationUtils;
    final private ModelMapper modelMapper;

    @Autowired
    public AdminService(AdminRepository adminRepository, AuthorizationUtils authorizationUtils, ModelMapper modelMapper) {
        this.adminRepository = adminRepository;
        this.authorizationUtils = authorizationUtils;
        this.modelMapper = modelMapper;
    }

    public List<AdminResponseDto> getAllAdmins() {
        // Verificación del rol de usuario
        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden ver la lista de administradores");
        }

        // Obtener solo los administradores (rol 0)
        List<Admin> admins = adminRepository.findAll();  // Filtrando por rol = 0 (ADMIN)
        return admins.stream()
                .map(admin -> modelMapper.map(admin, AdminResponseDto.class))
                .toList();
    }

    public AdminResponseDto getAdminById(Long id) {
        Admin admin = adminRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        return modelMapper.map(admin, AdminResponseDto.class);
    }

    public AdminSelfResponseDto getAdminOwnInfo() {
        String username = authorizationUtils.getCurrentUserEmail();
        if (username == null) {
            throw new ResourceNotFoundException("Usuario anónimo no tiene permiso de acceder a este recurso");
        }

        Admin admin = adminRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        return modelMapper.map(admin, AdminSelfResponseDto.class);
    }

    public AdminResponseDto createAdmin(AdminRequestDto adminRequestDto) {
        // Verificar si el usuario tiene el rol de administrador
        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("Solo los administradores pueden crear administradores");
        }

        Admin admin = modelMapper.map(adminRequestDto, Admin.class);
        admin.setRole(Role.ADMIN);

        Admin savedAdmin = adminRepository.save(admin);

        return modelMapper.map(savedAdmin, AdminResponseDto.class);
    }

    public AdminResponseDto updateAdmin(Long id, AdminPatchRequestDto adminPatchRequestDto) {
        Admin admin = adminRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        if (adminPatchRequestDto.getFirstName() != null) {
            admin.setFirstName(adminPatchRequestDto.getFirstName());
        }
        if (adminPatchRequestDto.getLastName() != null) {
            admin.setLastName(adminPatchRequestDto.getLastName());
        }
        if (adminPatchRequestDto.getPhoneNumber() != null) {
            admin.setPhoneNumber(adminPatchRequestDto.getPhoneNumber());
        }

        Admin updatedAdmin = adminRepository.save(admin);

        return modelMapper.map(updatedAdmin, AdminResponseDto.class);
    }

    public ResponseEntity<String> deleteAdmin(Long id) {
        // Verificar que el usuario tiene permisos para eliminar
        if (!authorizationUtils.isAdmin()) {
            throw new ResourceNotFoundException("El usuario no tiene permiso para eliminar este recurso");
        }

        // Verificar si el administrador existe
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Administrador con ID " + id + " no encontrado");
        }

        adminRepository.deleteById(id);

        // Retornar una respuesta con el mensaje de éxito
        return ResponseEntity.ok("Administrador con ID " + id + " eliminado con éxito");
    }

}
