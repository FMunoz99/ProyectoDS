package backend.estudiante.application;

import backend.estudiante.domain.EstudianteService;
import backend.estudiante.dto.EstudiantePatchRequestDto;
import backend.estudiante.dto.EstudianteRequestDto;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.estudiante.dto.EstudianteSelfResponseDto;
import backend.incidente.domain.IncidenteService;
import backend.incidente.dto.IncidenteResponseDto;
import backend.objetoPerdido.domain.ObjetoPerdidoService;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estudiante")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final IncidenteService incidenteService;
    private final ObjetoPerdidoService objetoPerdidoService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService, IncidenteService incidenteService,
                                ObjetoPerdidoService objetoPerdidoService) {
        this.estudianteService = estudianteService;
        this.incidenteService = incidenteService;
        this.objetoPerdidoService = objetoPerdidoService;
    }

    @GetMapping("/me")
    public ResponseEntity<EstudianteSelfResponseDto> getEstudiante() {
        return ResponseEntity.ok(estudianteService.getEstudianteOwnInfo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDto> getEstudiante(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.getEstudianteInfo(id));
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDto> createEstudiante(@RequestBody @Valid EstudianteRequestDto dto) {
        EstudianteResponseDto createdEstudiante = estudianteService.createEstudiante(dto);
        return new ResponseEntity<>(createdEstudiante, HttpStatus.CREATED);
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

    @GetMapping("/me/incidentes")
    public ResponseEntity<List<IncidenteResponseDto>> getEstudianteIncidentes() {
        List<IncidenteResponseDto> incidentes = incidenteService.getIncidentesByEstudiante();
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/me/objetos-perdidos")
    public ResponseEntity<List<ObjetoPerdidoResponseDto>> getEstudianteObjetosPerdidos() {
        List<ObjetoPerdidoResponseDto> objetosPerdidos = objetoPerdidoService.getObjetosPerdidosByEstudiante();
        return ResponseEntity.ok(objetosPerdidos);
    }

}
