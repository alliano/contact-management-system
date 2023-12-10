package com.restfull.restfullapi;

import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfull.restfullapi.dtos.ContactResponse;
import com.restfull.restfullapi.dtos.CreateContactRequest;
import com.restfull.restfullapi.dtos.UpdateContactRequest;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.Contact;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.ContactRepository;
import com.restfull.restfullapi.repositories.UserRepository;
import com.restfull.restfullapi.sec.BCrypt;

@SpringBootTest @AutoConfigureMockMvc
public class ContactControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private static final String TOKEN = "dhayigiya739q77qt9d3tq7d38qhx3q9ggq6rt86";

    @BeforeEach
    public void setUp() {
        this.contactRepository.deleteAll();
        this.userRepository.deleteAll();
        User user = User.builder()
                    .username("alliano-dev")
                    .name("alliano")
                    .password(BCrypt.hashpw("secret", BCrypt.gensalt()))
                    .token(TOKEN)
                    .tokenExpiredAt(System.currentTimeMillis() + (1000 * 60 * 24 * 30))
                    .build();
        this.userRepository.save(user);
    }

    @Test
    public void testCreateContactBadReq() throws JsonProcessingException, Exception {
        this.mockMvc.perform(
            post("/api/contact/create")
            .content(this.objectMapper.writeValueAsString(new CreateContactRequest()))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testCreateContactSuccess() throws JsonProcessingException, Exception {
        CreateContactRequest request = CreateContactRequest.builder()
                    .firstName("Abdillah")
                    .lastName("Alli")
                    .email("abdillah@gmail.com")
                    .phone("+6281341079104")
                    .build();

        this.mockMvc.perform(
            post("/api/contact/create")
            .content(this.objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});
            Assertions.assertNull(response.getErrors());
        });
    }

    @Test
    public void getContactNotFound() throws Exception {
     String contactId = UUID.randomUUID().toString();
        this.mockMvc.perform(
            get("/api/contact/"+contactId)
            .header("X-API-TOKEN", TOKEN)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testSucessGetContact() throws Exception {
        String contactId = UUID.randomUUID().toString();
        User user = this.userRepository.findById("alliano-dev").orElse(null);
        Contact contact = Contact.builder()
                          .id(contactId)
                          .firstName("Khalid")
                          .lastName("bin walid")
                          .email("walid@aljazair.com")
                          .phone("09122223782")
                          .user(user)
                          .build();
        this.contactRepository.save(contact);

        this.mockMvc.perform(
            get("/api/contact/".concat(contactId))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals(contactId, response.getData().getId());
            Assertions.assertEquals("Khalid", response.getData().getFirstName());
            Assertions.assertEquals("bin walid", response.getData().getLastName());
            Assertions.assertEquals("walid@aljazair.com", response.getData().getEmail());
            Assertions.assertEquals("09122223782", response.getData().getPhone());
        });
    }

    @Test
    public void testUpdateContactWithEmptyToken() throws JsonProcessingException, Exception {
        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        this.mockMvc.perform(
            put("/api/contact/308f9w")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testUpdateContactWithInvalidId() throws JsonProcessingException, Exception {
      UpdateContactRequest updateContactRequest = UpdateContactRequest.builder()
                  .id("120ed937y")
                  .firstName("test")
                  .lastName("test")
                  .email("test@gmail.com")
                  .phone("0129737642")
                  .build();
        
        this.mockMvc.perform(
            put("/api/contact/".concat("du93y7d"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            .content(this.objectMapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testUpdateContactSuccess() throws JsonProcessingException, Exception {
        String contactId = UUID.randomUUID().toString();
        User user = this.userRepository.findFirstByToken(TOKEN).orElse(null);
        Contact contact = Contact.builder()
                    .id(contactId)
                    .firstName("alliano")
                    .lastName("non")
                    .email("ekano@gmail.com")
                    .phone("08134107829")
                    .user(user)
                    .build();
        this.contactRepository.save(contact);
        UpdateContactRequest updateContactRequest = UpdateContactRequest.builder()
                    .firstName("alliano-dev")
                    .lastName("enginner")
                    .email("enginner@gmail.com")
                    .phone("081341079104")
                    .id(contactId)
                    .build();

        this.mockMvc.perform(
            put("/api/contact/".concat(contactId))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(updateContactRequest))
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("OK", response.getData());
        });
    }

    @Test
    public void deleteContactWithUnkownId() throws Exception {
        this.mockMvc.perform(
            delete("/api/contact/".concat("aidf93yq9"))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void deleteContactWithInvalidToken() throws Exception {
        String contactId = UUID.randomUUID().toString();
        Contact contact = Contact.builder()
                      .id(contactId)
                      .firstName("test")
                      .lastName("test-delete")
                      .email("test@gmail.com")
                      .phone("803542573321")
                      .user(this.userRepository.findFirstByToken(TOKEN).get())
                      .build();
        this.contactRepository.save(contact);
        this.mockMvc.perform(
            delete("/api/contact/".concat(contactId))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "invalid_token")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        String contactId = UUID.randomUUID().toString();
        Contact contact = Contact.builder()
                    .id(contactId)
                    .firstName("test_")
                    .lastName("test-")
                    .email("test@gmail.com")
                    .phone("39249239443")
                    .user(this.userRepository.findFirstByToken(TOKEN).get())
                    .build();
        this.contactRepository.save(contact);
        this.mockMvc.perform(
            delete("/api/contact/".concat(contactId))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getData());
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("OK", response.getData());
        });
    }

    @Test
    public void searchNotFound() throws Exception {
        this.mockMvc.perform(
            get("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals(0, response.getData().size());
            Assertions.assertEquals(0, response.getPaging().getTotalPage());
            Assertions.assertEquals(0, response.getPaging().getCurrentPage());
            Assertions.assertEquals(10, response.getPaging().getSize());
        });
    }

    @Test
    public void testSuccessSearchContact() throws Exception {
        User user = this.userRepository.findFirstByToken(TOKEN).get();
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        for (int i = 0; i < 100; i++) {
            Contact contact = Contact.builder()
                            .id(UUID.randomUUID().toString())
                            .firstName("alliano-"+isNull())
                            .lastName("test-"+i)
                            .email("test-"+i+"@gmail.com")
                            .user(user)
                            .build();
            contactList.add(contact);
        }
        this.contactRepository.saveAll(contactList);

        this.mockMvc.perform(
            get("/api/contact")
            .accept(MediaType.APPLICATION_JSON)
            .queryParam("name", "alliano")
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});
            Assertions.assertEquals(10, response.getData().size());
            Assertions.assertEquals(10, response.getPaging().getTotalPage());
            Assertions.assertEquals(0, response.getPaging().getCurrentPage());
            Assertions.assertEquals(10, response.getPaging().getSize());
        });
    }


}
