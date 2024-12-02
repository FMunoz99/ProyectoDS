package backend.noticias.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticiasResponseDto {

    private Long idNoticias;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaActualizacion;

    private Long adminId;
    private String adminNombre;
}
