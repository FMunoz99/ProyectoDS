package backend.objetoPerdido.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ObjetoPerdidoResponseDto {

    private Long id;
    private String piso;

    private String ubicacion;
    private EstadoReporte estadoReporte;

    private EstadoTarea estadoTarea;
    private String detalle;
    private String email;

    private String description;
    private String phoneNumber;

    private Long estudianteId;
    private Long empleadoId;
}
