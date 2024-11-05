package backend.empleado.dto;

import lombok.Data;

@Data
public class EmpleadoSelfResponseDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
