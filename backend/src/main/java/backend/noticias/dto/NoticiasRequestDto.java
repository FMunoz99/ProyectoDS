package backend.noticias.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticiasRequestDto {

    @NotNull(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no debe exceder los 255 caracteres")
    private String titulo;

    @NotNull(message = "El contenido es obligatorio")
    @Size(max = 1500, message = "El contenido no debe exceder los 1500 caracteres")
    private String contenido;

    private LocalDateTime fechaPublicacion;

    private Long adminId;
}
