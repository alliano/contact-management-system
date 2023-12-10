package com.restfull.restfullapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfull.restfullapi.dtos.RegisterUserRequest;
import com.restfull.restfullapi.dtos.UpdateUserRequest;
import com.restfull.restfullapi.dtos.UserResponse;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.UserRepository;
import com.restfull.restfullapi.sec.BCrypt;

@SpringBootTest @AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

 	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		this.userRepository.deleteAll();
	}

	@Test
	public void testRegiseterSuccess() throws Exception {
		RegisterUserRequest registerReq = RegisterUserRequest.builder()
				.username("test")
				.password("rehasia")
				.name("test")
				.build();
		
		this.mockMvc.perform(
			post("/api/users/register")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(registerReq))
		).andExpectAll(
			status().isOk()
		).andDo(result -> {
			WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
			Assertions.assertEquals("Ok", response.getData());
		});
	}

	@Test
	public void testRegiseterFaill() throws JsonProcessingException, Exception {
	RegisterUserRequest request = RegisterUserRequest.builder()
				.username("")
				.name("")
				.password("")
				.build();
		this.mockMvc.perform(
			post("/api/users/register")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(request))
		).andExpectAll(
			status().isBadRequest()
		).andDo(result -> {
			WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
			Assertions.assertNotNull(response.getErrors());
		});
	}

	@Test
	public void testDuplicateUsername() throws JsonProcessingException, Exception {
		RegisterUserRequest registrationReqBuilder = RegisterUserRequest.builder()
				.username("alliano-dev")
				.password(BCrypt.hashpw("awwoakNgembntod", BCrypt.gensalt()))
				.name("alliano")
				.build();
		User user = User.builder()
				.name("alliano")
				.username("alliano-dev")
				.password(BCrypt.hashpw("awoapjwaojbabsi", BCrypt.gensalt()))
				.build();
		this.userRepository.save(user);
		this.mockMvc.perform(
			post("/api/users/register")
			 .content(this.objectMapper.writeValueAsString(registrationReqBuilder))
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
		).andExpectAll(
			status().isBadRequest()
		).andDo(result -> {
			WebResponse<String> response = this.objectMapper.readValue(
				result.getResponse().getContentAsString(), 
				new TypeReference<WebResponse<String>>(){});
			Assertions.assertNotNull(response.getErrors());
			Assertions.assertEquals("user with " + registrationReqBuilder.getUsername()+" alredy exist, try another username", response.getErrors());
		});
	}

	@Test
    public void testGetCurrentUserWithInvalidToken() throws Exception {
        this.mockMvc.perform(
            get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("X-API-TOKEN", "invalidToken")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

	@Test
    public void testGetCurrentUserWithEmptyToken() throws Exception {
        this.mockMvc.perform(
            get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

	@Test
	public void getCurrentUserWithValidToken() throws Exception {
		String token = UUID.randomUUID().toString();
		User user = User.builder()
					.username("Abdillah Alli")
					.name("alli")
					.token(token)
					.password(BCrypt.hashpw("secret", BCrypt.gensalt()))
					.tokenExpiredAt(System.currentTimeMillis() + (1000 * 60 * 24 * 7))
					.build();
		this.userRepository.save(user);

		this.mockMvc.perform(
			get("/api/users/current")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header("X-API-TOKEN", token)
		).andExpectAll(
			status().isOk()
		).andDo(result -> {
			WebResponse<UserResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>(){});
			Assertions.assertNull(response.getErrors());
			Assertions.assertEquals("Abdillah Alli", response.getData().getUsername());
			Assertions.assertEquals("alli", response.getData().getName());
		});
	}

	@Test
	public void testGetUserWithExpiredToken() throws Exception {
		String token = UUID.randomUUID().toString();
		User user = User.builder()
					.username("Abdillah Alli")
					.name("alli")
					.token(token)
					.password(BCrypt.hashpw("secret", BCrypt.gensalt()))
					.tokenExpiredAt(System.currentTimeMillis() + -(1000 * 60 * 24 * 7))
					.build();
		this.userRepository.save(user);

		this.mockMvc.perform(
			get("/api/users/current")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header("X-API-TOKEN", token)
		).andExpectAll(
			status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
			Assertions.assertNotNull(response.getErrors());
		});
	}

	@Test
	public void updateCurrentUserWithEmpetyValueAndToken() throws JsonProcessingException, Exception {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest();
		this.mockMvc.perform(
			patch("/api/users/update")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(updateUserRequest))
		).andExpectAll(
			status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
			Assertions.assertNotNull(response.getErrors());
		});
	}

	@Test
	public void testUpdateCurrentUserWithValidRequest() throws JsonProcessingException, Exception {
		String token = UUID.randomUUID().toString();
		User user = User.builder()
					.username("aliano-dev")
					.name("alliano")
					.password(BCrypt.hashpw("secret_passwd", BCrypt.gensalt()))
					.token(token)
					.tokenExpiredAt(System.currentTimeMillis() + (1000 * 60 * 24 * 30))
					.build();
		this.userRepository.save(user);

		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
					.name("alliano-engine")
					.build();
		
		this.mockMvc.perform(
			patch("/api/users/update")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header("X-API-TOKEN", token)
			.content(this.objectMapper.writeValueAsString(updateUserRequest))
		).andExpectAll(
			status().isOk()
		).andDo(resutl -> {
			WebResponse<UserResponse> response = this.objectMapper.readValue(resutl.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>(){});
			Assertions.assertNull(response.getErrors());
			Assertions.assertNotNull(response.getData().getName());
			Assertions.assertNotNull(response.getData().getUsername());
			Assertions.assertEquals("alliano-engine", response.getData().getName());
		});
	}
}
