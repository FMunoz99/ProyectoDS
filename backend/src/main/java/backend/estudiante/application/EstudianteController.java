package backend.estudiante.application;

import backend.estudiante.domain.EstudianteService;
import backend.estudiante.dto.EstudiantePatchRequestDto;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.dto.EstudianteSelfResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiante")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping("/me")
    public ResponseEntity<EstudianteSelfResponseDto> getEstudiante() {
        return ResponseEntity.ok(estudianteService.getEstudianteOwnInfo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDto> getEstudiante(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.getEstudianteInfo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudiante(@PathVariable Long id) {
        estudianteService.deleteEstudiante(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<EstudianteResponseDto> updateEstudiante(@RequestBody EstudiantePatchRequestDto estudianteSelfResponseDto) {
        return ResponseEntity.ok().body(estudianteService.updateEstudiante(estudianteSelfResponseDto));
    }
}
