package backend.admin.dto;

import lombok.Data;

@Data
public class AdminSelfResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
