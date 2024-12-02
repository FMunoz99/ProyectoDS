package backend.noticias.application;

import backend.admin.domain.Admin;
import backend.noticias.domain.NoticiasService;
import backend.noticias.dto.NoticiasPatchRequestDto;
import backend.noticias.dto.NoticiasRequestDto;
import backend.noticias.dto.NoticiasResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noticias")
public class NoticiasController {

    final private NoticiasService noticiasService;

    @Autowired
    public NoticiasController(NoticiasService noticiasService) {
        this.noticiasService = noticiasService;
    }

    @GetMapping
    public ResponseEntity<List<NoticiasResponseDto>> getAllNoticias() {
        List<NoticiasResponseDto> noticias = noticiasService.getAllNoticias();
        return ResponseEntity.ok(noticias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticiasResponseDto> getNoticiaById(@PathVariable Long id) {
        NoticiasResponseDto noticia = noticiasService.getNoticiaById(id);
        return ResponseEntity.ok(noticia);
    }

    @PostMapping("/publicar")
    public ResponseEntity<NoticiasResponseDto> createNoticia(@RequestBody NoticiasRequestDto requestDto) {
        NoticiasResponseDto noticia = noticiasService.createNoticia(requestDto);
        return ResponseEntity.ok(noticia);
    }

    @PatchMapping("/editar-noticia/{id}")
    public ResponseEntity<NoticiasResponseDto> updateNoticia(
            @PathVariable Long id,
            @RequestBody NoticiasPatchRequestDto patchDto) {
        NoticiasResponseDto noticia = noticiasService.updateNoticia(id, patchDto);
        return ResponseEntity.ok(noticia);
    }
}
