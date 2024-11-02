package backend.estudiante.dto;

import lombok.Data;

@Data
public class EstudiantePatchRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
}
