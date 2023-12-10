package com.restfull.restfullapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfull.restfullapi.dtos.LoginUserRequest;
import com.restfull.restfullapi.dtos.RegisterUserRequest;
import com.restfull.restfullapi.dtos.TokenResponse;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.UserRepository;
import com.restfull.restfullapi.sec.BCrypt;
import com.restfull.restfullapi.services.UserService;

import lombok.SneakyThrows;

@SpringBootTest @AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        this.userRepository.deleteAll();
    }

    @Test
    public void testFailedLoginUserNotFound() throws JsonProcessingException, Exception {
       LoginUserRequest loginUserRequest = LoginUserRequest.builder().username("test").password("test").build();
        
       this.mockMvc.perform(
            post("/api/auth/authenticate")
            .content(this.objectMapper.writeValueAsString(loginUserRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
       ).andExpectAll(
            status().isUnauthorized()
       ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals("username or password wrong!", response.getErrors());
       });
    }

    @Test
    public void testLoginFailedWrongPassword() throws JsonProcessingException, Exception {
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                    .username("alliano-dev")
                    .password(BCrypt.hashpw("rahasia", BCrypt.gensalt()))
                    .name("alliano")
                    .build();
        this.userService.register(registerUserRequest);
        
        
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                    .username(registerUserRequest.getUsername())
                    .password("password salah")
                    .build();
        this.mockMvc.perform(
            post("/api/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals("username or password wrong!", response.getErrors());
        });
    }

    @Test
    public void testLoginUserPasswordBlank() throws JsonProcessingException, Exception {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder().build();
        this.mockMvc.perform(
            post("/api/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
            Assertions.assertTrue(response.getErrors().contains("must not be blank"));
        });
    }

    @Test
    public void testSuccessLogin() throws JsonProcessingException, Exception {
        String token = UUID.randomUUID().toString();
        Long tokenExpired = System.currentTimeMillis() + (1000 * 60 * 24 * 7);
		User user = User.builder()
					.username("Abdillah Alli")
					.name("alli")
					.token(token)
					.password(BCrypt.hashpw("secret", BCrypt.gensalt()))
					.tokenExpiredAt(tokenExpired)
					.build();
		this.userRepository.save(user);

        LoginUserRequest loginReq = LoginUserRequest.builder()
                    .username("Abdillah Alli")
                    .password("secret")
                    .build();

        this.mockMvc.perform(
            post("/api/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(loginReq))
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>(){});
            Assertions.assertNotNull(response.getData().getToken());
            Assertions.assertNotNull(response.getData().getExpiredAt());
            Assertions.assertNull(response.getErrors());
        });
    }

    @Test
    public void testSuccessLogout() throws Exception {
        String token = UUID.randomUUID().toString();
        User user = User.builder()
                    .username("alliano-dev")
                    .name("allia")
                    .password(BCrypt.hashpw("secret_pass", BCrypt.gensalt()))
                    .token(token)
                    .tokenExpiredAt(System.currentTimeMillis() + (1000 * 60 * 24 * 30))
                    .build();
        this.userRepository.save(user);
        this.mockMvc.perform(
            delete("/api/auth/logout")
            .header("X-API-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            User usr = this.userRepository.findById("alliano-dev").orElse(null);
            
            Assertions.assertNull(usr.getToken());
            Assertions.assertNull(usr.getTokenExpiredAt());
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("OK", response.getData());
        });
    }

    @Test @SneakyThrows
    public void testLongoutFaild(){
        this.mockMvc.perform(
            delete("/api/auth/logout")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }
}
