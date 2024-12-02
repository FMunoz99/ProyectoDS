package backend.incidente.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncidenteResponseDto {

    private Long id;

    private String piso;
    private String detalle;
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private EstadoReporte estadoReporte;

    @Enumerated(EnumType.STRING)
    private EstadoTarea estadoTarea;

    private String email;

    private String phoneNumber;
    private String description;

    private Long estudianteId;
    private Long empleadoId;

    private LocalDate fechaReporte;

    private String fotoIncidenteUrl;
}
