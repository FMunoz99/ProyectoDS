package backend.incidente.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncidenteRequestDto {

    @NotNull
    @Size(min = 1, max = 10)
    private String piso;

    @NotNull
    private String ubicacion;

    @NotNull
    private String detalle;

    @NotNull
    private String email;

    @NotNull
    @Size(min = 1, max = 15)
    private String phoneNumber;

    @Size(max = 255)
    private String description;

    @NotNull
    private LocalDate fechaReporte;

    @Enumerated(EnumType.STRING)
    private EstadoReporte estadoReporte;

    @Enumerated(EnumType.STRING)
    private EstadoTarea estadoTarea;
}
