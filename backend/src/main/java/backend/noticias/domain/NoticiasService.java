package backend.noticias.domain;

import backend.admin.domain.Admin;
import backend.admin.infrastructure.AdminRepository;
import backend.auth.utils.AuthorizationUtils;
import backend.estudiante.domain.Estudiante;
import backend.estudiante.exceptions.UnauthorizeOperationException;
import backend.exceptions.ResourceNotFoundException;
import backend.noticias.dto.NoticiasPatchRequestDto;
import backend.noticias.dto.NoticiasRequestDto;
import backend.noticias.dto.NoticiasResponseDto;
import backend.noticias.infrastructure.NoticiasRepository;
import backend.usuario.domain.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticiasService {

    private final NoticiasRepository noticiasRepository;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final AdminRepository adminRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public NoticiasService(NoticiasRepository noticiasRepository, ModelMapper modelMapper,
                           AuthorizationUtils authorizationUtils, AdminRepository adminRepository,
                           UsuarioService usuarioService) {
        this.noticiasRepository = noticiasRepository;
        this.modelMapper = modelMapper;
        this.authorizationUtils = authorizationUtils;
        this.adminRepository = adminRepository;
        this.usuarioService = usuarioService;
    }

    public List<NoticiasResponseDto> getAllNoticias() {
        // Verificar si el usuario tiene rol de admin
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden acceder a este recurso.");
        }

        // Obtener todas las noticias y mapearlas al DTO, incluyendo los datos del administrador
        return noticiasRepository.findAll().stream()
                .map(noticia -> {
                    NoticiasResponseDto dto = modelMapper.map(noticia, NoticiasResponseDto.class);

                    // Añadir información del administrador si existe
                    if (noticia.getAdmin() != null) {
                        dto.setAdminId(noticia.getAdmin().getId());
                        dto.setAdminNombre(noticia.getAdmin().getFirstName() + " " + noticia.getAdmin().getLastName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public NoticiasResponseDto getNoticiaById(Long id) {
        // Verificar si el usuario tiene rol de admin
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden acceder a este recurso.");
        }

        Noticias noticia = noticiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia con id " + id + " no encontrada"));

        // Mapear la entidad al DTO y agregar los datos del administrador
        NoticiasResponseDto dto = modelMapper.map(noticia, NoticiasResponseDto.class);
        if (noticia.getAdmin() != null) {
            dto.setAdminId(noticia.getAdmin().getId());
            dto.setAdminNombre(noticia.getAdmin().getFirstName() + " " + noticia.getAdmin().getLastName());
        }

        return dto;
    }


    public NoticiasResponseDto createNoticia(NoticiasRequestDto requestDto) {
        // Obtener el email del administrador autenticado
        String email = usuarioService.getCurrentUserEmail();

        // Buscar al administrador por email
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado para el email: " + email));

        // Crear la noticia
        Noticias noticia = new Noticias();
        noticia.setTitulo(requestDto.getTitulo());
        noticia.setContenido(requestDto.getContenido());
        noticia.setFechaPublicacion(LocalDateTime.now());
        noticia.setAdmin(admin); // Asociar al administrador

        // Guardar la noticia
        Noticias savedNoticia = noticiasRepository.save(noticia);

        // Mapear la entidad a un DTO y agregar la información del administrador
        NoticiasResponseDto responseDto = modelMapper.map(savedNoticia, NoticiasResponseDto.class);
        responseDto.setAdminId(admin.getId());
        responseDto.setAdminNombre(admin.getFirstName() + " " + admin.getLastName());

        return responseDto;
    }

    public NoticiasResponseDto updateNoticia(Long id, NoticiasPatchRequestDto patchDto) {
        // Verificar si el usuario tiene rol de admin
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden editar noticias.");
        }

        // Buscar la noticia por ID
        Noticias noticia = noticiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia con id " + id + " no encontrada"));

        // Actualizar los campos modificados
        if (patchDto.getTitulo() != null) {
            noticia.setTitulo(patchDto.getTitulo());
        }
        if (patchDto.getContenido() != null) {
            noticia.setContenido(patchDto.getContenido());
        }
        noticia.setFechaActualizacion(LocalDateTime.now());

        // Guardar la noticia actualizada
        Noticias updatedNoticia = noticiasRepository.save(noticia);

        // Mapear la entidad a un DTO y agregar la información del administrador
        NoticiasResponseDto responseDto = modelMapper.map(updatedNoticia, NoticiasResponseDto.class);

        // Añadir los datos del administrador si existen
        if (updatedNoticia.getAdmin() != null) {
            Admin admin = updatedNoticia.getAdmin();
            responseDto.setAdminId(admin.getId());
            responseDto.setAdminNombre(admin.getFirstName() + " " + admin.getLastName());
        }

        return responseDto;
    }

}
