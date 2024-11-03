package backend.objetoPerdido.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import lombok.Data;

@Data
public class ObjetoPerdidoPatchRequestDto {

    private EstadoReporte estadoReporte;
    private EstadoTarea estadoTarea;
}
