package backend.estudiante.dto;

import lombok.Data;

@Data
public class EstudianteSelfResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String fotoPerfilUrl;
}
