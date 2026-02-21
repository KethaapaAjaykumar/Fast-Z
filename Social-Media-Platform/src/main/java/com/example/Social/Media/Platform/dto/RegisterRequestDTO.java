package com.example.Social.Media.Platform.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
}
