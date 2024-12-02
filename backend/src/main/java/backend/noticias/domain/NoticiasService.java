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

        List<Noticias> noticias = noticiasRepository.findAll();
        return noticias.stream()
                .map(noticia -> modelMapper.map(noticia, NoticiasResponseDto.class))
                .collect(Collectors.toList());
    }

    public NoticiasResponseDto getNoticiaById(Long id) {
        // Verificar si el usuario tiene rol de admin
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden acceder a este recurso.");
        }

        Noticias noticia = noticiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia con id " + id + " no encontrada"));
        return modelMapper.map(noticia, NoticiasResponseDto.class);
    }

    public NoticiasResponseDto createNoticia(NoticiasRequestDto requestDto) {

        // Verificar si el usuario tiene rol de admin
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden acceder a este recurso.");
        }

        // Crear la noticia
        Noticias noticia = modelMapper.map(requestDto, Noticias.class);
        noticia.setTitulo(requestDto.getTitulo());
        noticia.setContenido(requestDto.getContenido());
        noticia.setFechaPublicacion(LocalDateTime.now());

        Noticias savedNoticia = noticiasRepository.save(noticia);
        return modelMapper.map(savedNoticia, NoticiasResponseDto.class);
    }


    public NoticiasResponseDto updateNoticia(Long id, NoticiasPatchRequestDto patchDto) {
        // Verificar si el usuario tiene rol de admin
        if (!authorizationUtils.isAdmin()) {
            throw new UnauthorizeOperationException("Solo los administradores pueden editar noticias.");
        }

        Noticias noticia = noticiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia con id " + id + " no encontrada"));

        if (patchDto.getTitulo() != null) {
            noticia.setTitulo(patchDto.getTitulo());
        }
        if (patchDto.getContenido() != null) {
            noticia.setContenido(patchDto.getContenido());
        }
        noticia.setFechaActualizacion(LocalDateTime.now());

        Noticias updatedNoticia = noticiasRepository.save(noticia);
        return modelMapper.map(updatedNoticia, NoticiasResponseDto.class);
    }
}
