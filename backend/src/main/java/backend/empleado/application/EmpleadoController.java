package backend.empleado.application;

import backend.empleado.domain.EmpleadoService;
import backend.empleado.dto.EmpleadoPatchRequestDto;
import backend.empleado.dto.EmpleadoResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empleado")
public class EmpleadoController {

    final private EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) { this.empleadoService = empleadoService; }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> getEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.getEmpleadoInfo(id));
    }

    @GetMapping("/me")
    public ResponseEntity<EmpleadoResponseDto> getEmpleado() {
        return ResponseEntity.ok(empleadoService.getEmpleadoOwnInfo());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        empleadoService.deleteEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> updateEmleadoInfo(@PathVariable Long id,
                                                                 @RequestBody EmpleadoPatchRequestDto empleadoInfo) {
        return ResponseEntity.ok().body(empleadoService.updateEmpleadoInfo(id, empleadoInfo));
    }
}
