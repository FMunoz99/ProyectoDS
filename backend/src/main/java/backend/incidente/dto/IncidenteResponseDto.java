package backend.incidente.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import lombok.Data;

@Data
public class IncidenteResponseDto {

    private Long id;

    private String piso;
    private String detalle;
    private String ubicacion;

    private EstadoReporte estadoReporte;
    private EstadoTarea estadoTarea;
    private String email;

    private String phoneNumber;
    private String description;

    private Long estudianteId;
    private Long empleadoId;
}
