package com.restfull.restfullapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @Builder
@Setter @Getter @AllArgsConstructor
public class LoginUserRequest {
    
    @NotBlank @Size(max = 100)
    private String username;

    @NotBlank @Size(max = 100)
    private String password;
}
