package backend.empleado.application;

import backend.empleado.domain.EmpleadoService;
import backend.empleado.dto.EmpleadoPatchRequestDto;
import backend.empleado.dto.EmpleadoRequestDto;
import backend.empleado.dto.EmpleadoResponseDto;
import backend.empleado.dto.EmpleadoSelfResponseDto;
import backend.estudiante.dto.EstudianteResponseDto;
import backend.incidente.dto.IncidenteResponseDto;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleado")
public class EmpleadoController {

    final private EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) { this.empleadoService = empleadoService; }

    @GetMapping("/lista")
    public ResponseEntity<List<EmpleadoResponseDto>> getAllEmpleados() {
        List<EmpleadoResponseDto> empleados = empleadoService.getAllEmpleados();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> getEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.getEmpleadoInfo(id));
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponseDto> createEmpleado(@RequestBody @Valid EmpleadoRequestDto dto) {
        EmpleadoResponseDto createdEmpleado = empleadoService.createEmpleado(dto);
        return new ResponseEntity<>(createdEmpleado, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<EmpleadoSelfResponseDto> getEmpleado() {
        return ResponseEntity.ok(empleadoService.getEmpleadoOwnInfo());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmpleado(@PathVariable Long id) {
        return empleadoService.deleteEmpleado(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> updateEmpleadoInfo(@PathVariable Long id,
                                                                 @RequestBody EmpleadoPatchRequestDto empleadoInfo) {
        return ResponseEntity.ok().body(empleadoService.updateEmpleadoInfo(id, empleadoInfo));
    }

    // NUEVOS CONTROLADORES

    @GetMapping("/me/incidentes")
    public ResponseEntity<List<IncidenteResponseDto>> getIncidentesAsignados() {
        List<IncidenteResponseDto> incidentes = empleadoService.getIncidentesAsignados();
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/me/objetos-perdidos")
    public ResponseEntity<List<ObjetoPerdidoResponseDto>> getObjetosPerdidosAsignados() {
        List<ObjetoPerdidoResponseDto> objetosPerdidos = empleadoService.getObjetosPerdidosAsignados();
        return ResponseEntity.ok(objetosPerdidos);
    }
}
