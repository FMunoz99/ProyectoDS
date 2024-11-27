package backend.empleado.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmpleadoSelfResponseDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Map<String, String> horarioDeTrabajo;
}
