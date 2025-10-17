package com.medichub.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
}
