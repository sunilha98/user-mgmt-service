package com.resourcemanagement.usermgmt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private Long id;
    private String email;
    private String username;
    private String roles;
    private String accessToken;
}
