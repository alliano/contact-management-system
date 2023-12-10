package com.restfull.restfullapi.services;

import com.restfull.restfullapi.dtos.RegisterUserRequest;
import com.restfull.restfullapi.dtos.UpdateUserRequest;
import com.restfull.restfullapi.dtos.UserResponse;
import com.restfull.restfullapi.entities.User;

public interface UserService {
    
    public void register(RegisterUserRequest request);

    public UserResponse get(User user);

    public UserResponse update(User user, UpdateUserRequest request);
}
