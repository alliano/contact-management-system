package com.restfull.restfullapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class RegisterUserRequest {
    
    @NotBlank @Size(max = 100, min = 2)
    private String username;
    
    @NotBlank @Size(max = 100, min = 2)
    private String password;
    
    @NotBlank @Size(max = 100, min = 2)
    private String name;
}
