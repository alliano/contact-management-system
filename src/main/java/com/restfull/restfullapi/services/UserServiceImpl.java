package com.restfull.restfullapi.services;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.restfull.restfullapi.dtos.RegisterUserRequest;
import com.restfull.restfullapi.dtos.UpdateUserRequest;
import com.restfull.restfullapi.dtos.UserResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.UserRepository;
import com.restfull.restfullapi.sec.BCrypt;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service @AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationService validationService;

    @Override @Transactional
    public void register(RegisterUserRequest request) {
        this.validationService.validate(request);
        if(this.userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user with " + request.getUsername()+" alredy exist, try another username");
        }

        User user = User.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .name(request.getName())
                    .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                    .build();
        this.userRepository.save(user);
    }

    @Override
    public UserResponse get(User user) {
       return UserResponse.builder()
            .username(user.getUsername())
            .name(user.getName())
            .build();
    }

    @Override @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        this.validationService.validate(request);
        if(Objects.nonNull(request.getName())) user.setName(request.getName());
        if(Objects.nonNull(request.getPassword())) user.setName(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));

        System.out.println("name : "+user.getName());
        System.out.println("username : "+user.getUsername());

        this.userRepository.save(user);

        return UserResponse.builder()
            .name(user.getName())
            .username(user.getUsername())
            .build();

    }
    
}
