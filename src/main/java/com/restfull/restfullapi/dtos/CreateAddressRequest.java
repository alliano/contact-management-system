package com.restfull.restfullapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class CreateAddressRequest {
    
    @JsonIgnore @NotBlank
    private String contactId;

    @Size(max = 100)
    private String street;

    @Size(max = 100)
    private String city;
    
    @Size(max = 100)
    private String province;
    
    @Size(max = 100) @NotBlank
    private String country;
    
    @Size(max = 10)
    private String postalCode;
}
