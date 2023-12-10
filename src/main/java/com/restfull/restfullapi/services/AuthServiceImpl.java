package com.restfull.restfullapi.services;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.restfull.restfullapi.dtos.LoginUserRequest;
import com.restfull.restfullapi.dtos.TokenResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.UserRepository;
import com.restfull.restfullapi.sec.BCrypt;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service @AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final ValidationService validationService;

    @Override @Transactional
    public TokenResponse login(LoginUserRequest request) {
        this.validationService.validate(request);
        User user = this.userRepository.findById(request.getUsername())
                        .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, 
                            "username or password wrong!"));
        // if password match
        if(BCrypt.checkpw(request.getPassword(), user.getPassword() )) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            System.out.println(user.getName());
            return TokenResponse.builder().token(user.getToken()).expiredAt(user.getTokenExpiredAt()).build();
        }
        // if password doesn't match
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong!");
        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }

    @Override @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);
        this.userRepository.save(user);
    }
    
}
