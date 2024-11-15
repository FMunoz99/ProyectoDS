package backend.admin.dto;

import lombok.Data;

@Data
public class AdminResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
