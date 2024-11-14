package backend.incidente.application;

import backend.incidente.domain.Incidente;
import backend.incidente.domain.IncidenteService;
import backend.incidente.dto.IncidentePatchRequestDto;
import backend.incidente.dto.IncidenteRequestDto;
import backend.incidente.dto.IncidenteResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidente")
public class IncidenteController {

    final private IncidenteService incidenteService;

    @Autowired
    public IncidenteController(IncidenteService incidenteService) {
        this.incidenteService = incidenteService;
    }

    @GetMapping
    public ResponseEntity<List<IncidenteResponseDto>> getAllIncidentes() {
        List<IncidenteResponseDto> incidentes = incidenteService.findAllIncidentes();
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenteResponseDto> getIncidenteById(@PathVariable Long id) {
        IncidenteResponseDto incidente = incidenteService.findIncidenteById(id);
        return ResponseEntity.ok(incidente);
    }

    @PostMapping
    public ResponseEntity<IncidenteResponseDto> createIncidente(@RequestBody IncidenteRequestDto requestDto) {
        IncidenteResponseDto savedIncidente = incidenteService.saveIncidente(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIncidente);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<IncidenteResponseDto> updateIncidenteStatus(
            @PathVariable Long id,
            @RequestBody IncidentePatchRequestDto patchDto) {
        IncidenteResponseDto updatedIncidente = incidenteService.updateStatusIncidente(id, patchDto);
        return ResponseEntity.ok(updatedIncidente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncidente(@PathVariable Long id) {
        incidenteService.deleteIncidente(id);
        return ResponseEntity.noContent().build();
    }
}
