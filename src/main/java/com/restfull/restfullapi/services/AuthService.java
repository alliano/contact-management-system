package com.restfull.restfullapi.services;

import com.restfull.restfullapi.dtos.LoginUserRequest;
import com.restfull.restfullapi.dtos.TokenResponse;
import com.restfull.restfullapi.entities.User;

public interface AuthService {
    
    public TokenResponse login(LoginUserRequest request);

    public void logout(User user);
}
