package com.restfull.restfullapi.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restfull.restfullapi.dtos.LoginUserRequest;
import com.restfull.restfullapi.dtos.TokenResponse;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.services.AuthService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController @RequestMapping(path = "/api/auth", method = RequestMethod.GET)
public class Authentication {
    
    private final AuthService authService;

    @PostMapping(path = "/authenticate")
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        return WebResponse.<TokenResponse>builder().data(this.authService.login(request)).build();
    }

    @DeleteMapping(path = "/logout")
    public WebResponse<String> logout(User user) {
        this.authService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }


}
