package backend.empleado.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmpleadoResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String fotoPerfilUrl;
    private Map<String, String> horarioDeTrabajo;
}
