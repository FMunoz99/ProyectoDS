package backend.incidente.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class IncidentePatchRequestDto {

    @Enumerated(EnumType.STRING)
    private EstadoReporte estadoReporte;

    @Enumerated(EnumType.STRING)
    private EstadoTarea estadoTarea;
}
