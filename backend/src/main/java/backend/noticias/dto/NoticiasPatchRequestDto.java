package backend.noticias.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticiasPatchRequestDto {

    private String titulo;
    private String contenido;
    private LocalDateTime fechaActualizacion;
}
