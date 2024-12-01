package backend.admin.dto;

import lombok.Data;

@Data
public class AdminPatchRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
}
