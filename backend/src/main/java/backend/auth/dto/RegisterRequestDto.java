package backend.auth.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Boolean isEmpleado = false;
    private Boolean isAdmin = false;
}
