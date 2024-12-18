package backend.admin.application;

import backend.admin.component.ReportCounter;
import backend.admin.domain.AdminService;
import backend.admin.dto.AdminPatchRequestDto;
import backend.admin.dto.AdminRequestDto;
import backend.admin.dto.AdminResponseDto;
import backend.admin.dto.AdminSelfResponseDto;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import backend.incidente.domain.IncidenteService;
import backend.incidente.dto.IncidenteResponseDto;
import backend.objetoPerdido.domain.ObjetoPerdidoService;
import backend.objetoPerdido.dto.ObjetoPerdidoResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    final private AdminService adminService;
    final private IncidenteService incidenteService;
    final private ObjetoPerdidoService objetoPerdidoService;
    final private ReportCounter reportCounter;

    @Autowired
    public AdminController(AdminService adminService, IncidenteService incidenteService,
                           ObjetoPerdidoService objetoPerdidoService, ReportCounter reportCounter) {
        this.adminService = adminService;
        this.incidenteService = incidenteService;
        this.objetoPerdidoService = objetoPerdidoService;
        this.reportCounter = reportCounter;
    }


    @GetMapping("/me")
    public ResponseEntity<AdminSelfResponseDto> getCurrentAdmin() {
        return ResponseEntity.ok(adminService.getAdminOwnInfo());
    }

    @PostMapping
    public ResponseEntity<AdminResponseDto> createAdmin(@Valid @RequestBody AdminRequestDto adminRequestDto) {
        AdminResponseDto createdAdmin = adminService.createAdmin(adminRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminResponseDto> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminPatchRequestDto adminPatchRequestDto) {
        AdminResponseDto updatedAdmin = adminService.updateAdmin(id, adminPatchRequestDto);
        return updatedAdmin != null ? ResponseEntity.ok(updatedAdmin) : ResponseEntity.notFound().build();
    }


    // nuevos controladores

    // Endpoint para obtener reportes de incidentes por estado
    @GetMapping("/reportes/incidentes/aceptados")
    public ResponseEntity<List<IncidenteResponseDto>> getIncidentesAceptados() {
        List<IncidenteResponseDto> incidentes = incidenteService.getIncidentesPorEstado(EstadoReporte.ACEPTADO);
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/reportes/objetos-perdidos/aceptados")
    public ResponseEntity<List<ObjetoPerdidoResponseDto>> getObjetosPerdidosAceptados() {
        List<ObjetoPerdidoResponseDto> objetos = objetoPerdidoService.getObjetosPerdidosPorEstado(EstadoReporte.ACEPTADO);
        return ResponseEntity.ok(objetos);
    }

    @GetMapping("/reportes/incidentes/finalizados")
    public ResponseEntity<List<IncidenteResponseDto>> getIncidentesFinalizados() {
        List<IncidenteResponseDto> incidentes = incidenteService.getIncidentesPorEstadoTarea(EstadoTarea.FINALIZADO);
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/reportes/objetos-perdidos/finalizados")
    public ResponseEntity<List<ObjetoPerdidoResponseDto>> getObjetosPerdidosFinalizados() {
        List<ObjetoPerdidoResponseDto> objetos = objetoPerdidoService.getObjetosPerdidosPorEstadoTarea(EstadoTarea.FINALIZADO);
        return ResponseEntity.ok(objetos);
    }

    @GetMapping("/reportes/incidentes/no-finalizados")
    public ResponseEntity<List<IncidenteResponseDto>> getIncidentesNoFinalizados() {
        List<IncidenteResponseDto> incidentes = incidenteService.getIncidentesPorEstadoTarea(EstadoTarea.NO_FINALIZADO);
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/reportes/objetos-perdidos/no-finalizados")
    public ResponseEntity<List<ObjetoPerdidoResponseDto>> getObjetosPerdidosNoFinalizados() {
        List<ObjetoPerdidoResponseDto> objetos = objetoPerdidoService.getObjetosPerdidosPorEstadoTarea(EstadoTarea.NO_FINALIZADO);
        return ResponseEntity.ok(objetos);
    }

    // Endpoint para obtener reportes por ID de estudiante
    @GetMapping("/reportes/estudiante/{id}/incidentes")
    public ResponseEntity<List<IncidenteResponseDto>> getIncidentesPorEstudiante(@PathVariable Long id) {
        List<IncidenteResponseDto> incidentes = incidenteService.getIncidentesPorEstudiante(id);
        return ResponseEntity.ok(incidentes);
    }

    @GetMapping("/reportes/estudiante/{id}/objetos-perdidos")
    public ResponseEntity<List<ObjetoPerdidoResponseDto>> getObjetosPerdidosPorEstudiante(@PathVariable Long id) {
        List<ObjetoPerdidoResponseDto> objetos = objetoPerdidoService.getObjetosPerdidosPorEstudiante(id);
        return ResponseEntity.ok(objetos);
    }


    @GetMapping("/dashboard/reportes-general")
    public ResponseEntity<Map<String, Object>> getReportes() {
        Map<String, Object> response = new HashMap<>();
        response.put("incidentesPorDia", reportCounter.getIncidentesPorDia());
        response.put("objetosPerdidosPorDia", reportCounter.getObjetosPerdidosPorDia());
        return ResponseEntity.ok(response);
    }

}
