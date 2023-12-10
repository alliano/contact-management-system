package com.restfull.restfullapi.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restfull.restfullapi.dtos.RegisterUserRequest;
import com.restfull.restfullapi.dtos.UpdateUserRequest;
import com.restfull.restfullapi.dtos.UserResponse;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.services.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController @RequestMapping(path = "/api/users", method = RequestMethod.GET)
public class UserController {
    
    private final UserService userService;

    @PostMapping(path = "/register",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        this.userService.register(request);
        return WebResponse.<String>builder().data("Ok").build();
    }

    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> get(User user) {
        return WebResponse.<UserResponse>builder().data(this.userService.get(user)).build();
    }

    @PatchMapping(path = "/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
        UserResponse userResponse = this.userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}
