package backend.incidente.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import lombok.Data;

@Data
public class IncidentePatchRequestDto {

    private EstadoReporte estadoReporte;
    private EstadoTarea estadoTarea;
}
