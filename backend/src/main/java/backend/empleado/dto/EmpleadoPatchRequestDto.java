package backend.empleado.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmpleadoPatchRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Map<String, String> horarioDeTrabajo;
}
