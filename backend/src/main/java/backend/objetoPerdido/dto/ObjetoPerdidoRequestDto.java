package backend.objetoPerdido.dto;

import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ObjetoPerdidoRequestDto {

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

    private String fotoObjetoPerdidoUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaReporte;

    private EstadoReporte estadoReporte;

    private EstadoTarea estadoTarea;
}
