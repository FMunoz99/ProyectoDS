package backend.objetoPerdido.application;

import backend.objetoPerdido.domain.ObjetoPerdido;
import backend.objetoPerdido.domain.ObjetoPerdidoService;
import backend.objetoPerdido.dto.ObjetoPerdidoPatchRequestDto;
import backend.objetoPerdido.dto.ObjetoPerdidoRequestDto;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/objetoPerdido")
public class ObjetoPerdidoController {

    private final ObjetoPerdidoService objetoPerdidoService;

    @Autowired
    public ObjetoPerdidoController(ObjetoPerdidoService objetoPerdidoService) {
        this.objetoPerdidoService = objetoPerdidoService;
    }

    @GetMapping
    public List<ObjetoPerdidoResponseDto> getAllObjetosPerdidos() {
        return objetoPerdidoService.findAllObjetosPerdidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetoPerdidoResponseDto> getObjetoPerdidoById(@PathVariable Long id) {
        ObjetoPerdidoResponseDto responseDto = objetoPerdidoService.findObjetoPerdidoById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<ObjetoPerdidoResponseDto> saveObjetoPerdido(
            @RequestParam("objetoPerdido") String objetoPerdidoJson,
            @RequestParam(value = "fotoObjetoPerdido", required = false) MultipartFile fotoObjetoPerdido) throws IOException {

        // Deserializa manualmente el JSON
        ObjectMapper objectMapper = new ObjectMapper();
        ObjetoPerdidoRequestDto requestDto = objectMapper.readValue(objetoPerdidoJson, ObjetoPerdidoRequestDto.class);

        ObjetoPerdidoResponseDto responseDto = objetoPerdidoService.saveObjetoPerdido(requestDto, fotoObjetoPerdido);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<ObjetoPerdidoResponseDto> updateObjetoPerdidoStatus(@PathVariable Long id,
                                                                              @RequestBody ObjetoPerdidoPatchRequestDto patchDto) {
        ObjetoPerdidoResponseDto responseDto = objetoPerdidoService.updateStatusObjetoPerdido(id, patchDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjetoPerdido(@PathVariable Long id) {
        objetoPerdidoService.deleteObjetoPerdido(id);
        return ResponseEntity.noContent().build();
    }
}
