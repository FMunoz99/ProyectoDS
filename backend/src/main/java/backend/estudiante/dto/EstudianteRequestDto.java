package backend.estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EstudianteRequestDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    @Size(min = 10, max = 15)
    private String phoneNumber;

    private String password;
}
