package com.restfull.restfullapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @Builder
public class TokenResponse {
    
    private String token;

    private Long expiredAt;
}
