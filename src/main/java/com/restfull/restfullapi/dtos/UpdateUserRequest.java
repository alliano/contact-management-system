package com.restfull.restfullapi.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UpdateUserRequest {
    
    @Size(max = 100)
    private String name;
    
    @Size(max = 100)
    private String password;
}
