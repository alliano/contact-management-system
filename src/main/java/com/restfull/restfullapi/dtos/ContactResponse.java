package com.restfull.restfullapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ContactResponse {
    
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}
