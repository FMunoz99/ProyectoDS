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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/lista")
    public ResponseEntity<List<EstudianteResponseDto>> getAllEstudiantes() {
        List<EstudianteResponseDto> estudiantes = estudianteService.getAllEstudiantes();
        return ResponseEntity.ok(estudiantes);
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
    public ResponseEntity<String> deleteEstudiante(@PathVariable Long id) {
        return estudianteService.deleteEstudiante(id);
    }

    @PatchMapping("/me")
    public ResponseEntity<EstudianteResponseDto> updateEstudiante(
            @RequestParam("estudiante") String estudianteJson,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) throws IOException {

        // Deserializa manualmente el JSON
        ObjectMapper objectMapper = new ObjectMapper();
        EstudiantePatchRequestDto estudiantePatchRequestDto = objectMapper.readValue(estudianteJson, EstudiantePatchRequestDto.class);

        EstudianteResponseDto updatedEstudiante = estudianteService.updateEstudiante(estudiantePatchRequestDto, fotoPerfil);
        return ResponseEntity.ok(updatedEstudiante);
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
