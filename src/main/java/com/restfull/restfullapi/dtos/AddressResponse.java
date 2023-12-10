package com.restfull.restfullapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class AddressResponse {
    
    private String id;

    private String street;

    private String city;

    private String province;

    private String country;

    private String postalCode;
}
