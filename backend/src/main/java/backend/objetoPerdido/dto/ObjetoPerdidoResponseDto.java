package backend.objetoPerdido.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
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
    private String phoneNumber;
}
