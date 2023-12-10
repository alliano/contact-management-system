package com.restfull.restfullapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class UpdateContactRequest {
    
    @JsonIgnore // agar diaabaikan sama mapper
    @NotBlank @Size(max = 100)
    private String id;

    @NotBlank @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;
    
    @Size(max = 100) @Email
    private String email;
    
    @Size(max = 100)
    private String phone;
}
