package backend.empleado.dto;

import lombok.Data;

@Data
public class EmpleadoPatchRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
}
