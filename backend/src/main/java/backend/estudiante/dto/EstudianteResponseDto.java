package backend.estudiante.dto;

import lombok.Data;

@Data
public class EstudianteResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    private String fotoPerfilUrl;
}
