package com.restfull.restfullapi;

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
import com.restfull.restfullapi.dtos.AddressResponse;
import com.restfull.restfullapi.dtos.CreateAddressRequest;
import com.restfull.restfullapi.dtos.UpdateAddressRequest;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.Address;
import com.restfull.restfullapi.entities.Contact;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.AddresseRepository;
import com.restfull.restfullapi.repositories.ContactRepository;
import com.restfull.restfullapi.repositories.UserRepository;
import com.restfull.restfullapi.sec.BCrypt;

@SpringBootTest @AutoConfigureMockMvc
public class AddressControllerTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private AddresseRepository addresseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "hdey9r729792hfd92yd923dy97y32e7y32he";

    private static final String CONTACT_ID = "jfd4feh927yr972y7rsxjvbvr286423648738f";

    @BeforeEach
    public void setUp() {
        this.addresseRepository.deleteAll();
        this.contactRepository.deleteAll();
        this.userRepository.deleteAll();

        User user = User.builder()
                    .username("alliano-dev")
                    .name("alliano")
                    .password(BCrypt.hashpw("secret_pw", BCrypt.gensalt()))
                    .token(TOKEN)
                    .tokenExpiredAt(System.currentTimeMillis() + (1000 * 60 * 24 * 30))
                    .build();
        this.userRepository.save(user);
        Contact contact = Contact.builder()
                    .id(CONTACT_ID)
                    .user(user)
                    .firstName("friend")
                    .lastName("fake")
                    .email("F713ND@gmail.com")
                    .phone("0283749791")
                    .build();
        this.contactRepository.save(contact);
    }

    @Test
    public void testCreateAddressBadReq() throws JsonProcessingException, Exception {
        this.mockMvc.perform(
            post("/api/contacts/".concat(CONTACT_ID).concat("/addresses"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            .content(this.objectMapper.writeValueAsString(new CreateAddressRequest()))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testCreateAddressSucess() throws JsonProcessingException, Exception {
        CreateAddressRequest createAddressRequest = CreateAddressRequest.builder()
                    .city("Jarusalem")
                    .country("Palestine")
                    .postalCode("90342")
                    .province("Jarusalem")
                    .street("al-quds")
                    .build();

        this.mockMvc.perform(
            post("/api/contacts/".concat(CONTACT_ID).concat("/addresses"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(createAddressRequest))
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});
            Assertions.assertNull(response.getErrors());
            Assertions.assertTrue(this.addresseRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    public void getAddressWithNotFoundAddressId() throws Exception {
        this.mockMvc.perform(
            get("/api/contacts/".concat(CONTACT_ID).concat("/addresses").concat("/invalid_address_id"))
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
    public void testGetAddressSuccess() throws Exception {
        String addressId = UUID.randomUUID().toString();
        Address address = Address.builder()
                    .id(addressId)
                    .city("Al-Quds")
                    .province("Palestine")
                    .country("Palestine")
                    .postalCode("0291387")
                    .contact(this.contactRepository.findFirstByUserAndId(this.userRepository.findFirstByToken(TOKEN).get(), CONTACT_ID).get())
                    .build();
        this.addresseRepository.save(address);

        this.mockMvc.perform(
            get("/api/contacts/".concat(CONTACT_ID).concat("/addresses/").concat(addressId))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("Palestine", response.getData().getCountry());
        });
    }

    @Test
    public void testUpdateAddressBadReq() throws JsonProcessingException, Exception {
        String addressId = UUID.randomUUID().toString();
        Address address = Address.builder()
                    .id(addressId)
                    .city("Al-Quds")
                    .province("Palestine")
                    .country("Palestine")
                    .postalCode("0291387")
                    .contact(this.contactRepository.findFirstByUserAndId(this.userRepository.findFirstByToken(TOKEN).get(), CONTACT_ID).get())
                    .build();
        this.addresseRepository.save(address);

        UpdateAddressRequest updateReq = UpdateAddressRequest.builder()
                                .city("Al-Quds")
                                .province("Palestine-2")
                                .postalCode("219837")
                                .build();
        this.mockMvc.perform(
            put("/api/contacts/".concat(CONTACT_ID).concat("/addresses/").concat(addressId))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            .content(this.objectMapper.writeValueAsString(updateReq))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(reuslt -> {
            WebResponse<AddressResponse> response = this.objectMapper.readValue(reuslt.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testUpdateSuccess() throws JsonProcessingException, Exception {
        String addressId = UUID.randomUUID().toString();
        Address address = Address.builder()
                    .id(addressId)
                    .city("Al-Quds")
                    .province("Palestine")
                    .country("Palestine")
                    .postalCode("0291387")
                    .contact(this.contactRepository.findFirstByUserAndId(this.userRepository.findFirstByToken(TOKEN).get(), CONTACT_ID).get())
                    .build();
        this.addresseRepository.save(address);

        UpdateAddressRequest updateReq = UpdateAddressRequest.builder()
                                .city("Al-Quds")
                                .country("Palestine-2")
                                .province("Palestine-2")
                                .postalCode("219837")
                                .build();
        this.mockMvc.perform(
            put("/api/contacts/".concat(CONTACT_ID).concat("/addresses/").concat(addressId))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            .content(this.objectMapper.writeValueAsString(updateReq))
        ).andExpectAll(
            status().isOk()
        ).andDo(reuslt -> {
            WebResponse<AddressResponse> response = this.objectMapper.readValue(reuslt.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});
            Assertions.assertNull(response.getErrors());
        });
    }

    @Test
    public void delteAddressNotFound() throws Exception {
        this.mockMvc.perform(
            delete("/api/contacts/".concat(CONTACT_ID).concat("/addresses").concat("/invalid_address_id"))
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
    public void testSuccessDelete() throws Exception {
        String addressId = UUID.randomUUID().toString();
        Address address = Address.builder()
                    .id(addressId)
                    .city("Al-Quds")
                    .province("Palestine")
                    .country("Palestine")
                    .postalCode("0291387")
                    .contact(this.contactRepository.findFirstByUserAndId(this.userRepository.findFirstByToken(TOKEN).get(), CONTACT_ID).get())
                    .build();
        this.addresseRepository.save(address);
        this.mockMvc.perform(
            delete("/api/contacts/".concat(CONTACT_ID).concat("/addresses/").concat(addressId))
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNull(response.getErrors());
            Assertions.assertFalse(this.addresseRepository.existsById(addressId));
        });
    }

    @Test
    public void getAddressNotFound() throws Exception {
        this.mockMvc.perform(
            get("/api/contacts/invalid_contactId/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testSuccessGetAddressList() throws Exception {
        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Address address = Address.builder()
                    .id(UUID.randomUUID().toString())
                    .city("Al-Quds"+i)
                    .province("Palestine"+i)
                    .country("Palestine"+i)
                    .postalCode("0291387"+i)
                    .contact(this.contactRepository.findFirstByUserAndId(this.userRepository.findFirstByToken(TOKEN).get(), CONTACT_ID).get())
                    .build();
            addresses.add(address);
            this.addresseRepository.saveAll(addresses);

            this.mockMvc.perform(
            get("/api/contacts/"+ CONTACT_ID +"/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", TOKEN)
            ).andExpectAll(
                status().isOk()
            ).andDo(result -> {
                WebResponse<List<AddressResponse>> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<AddressResponse>>>(){});
                Assertions.assertNull(response.getErrors());
            });
        }
    }
}
