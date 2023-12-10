package com.restfull.restfullapi.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restfull.restfullapi.dtos.ContactResponse;
import com.restfull.restfullapi.dtos.CreateContactRequest;
import com.restfull.restfullapi.dtos.PagingResponse;
import com.restfull.restfullapi.dtos.SearchContactRequest;
import com.restfull.restfullapi.dtos.UpdateContactRequest;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.services.ContactService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController @RequestMapping(path = "/api/contact")
public class ContactController {

    private final ContactService contactService;
    
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> createContact(User user, @RequestBody CreateContactRequest request) {
        ContactResponse contact = this.contactService.createContact(user, request);
        return WebResponse.<ContactResponse>builder().data(contact).build();
    }
    
    @GetMapping(path = "/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> getContact(User user, @PathVariable(name = "contactId") String contactId){
        return WebResponse.<ContactResponse>builder().data(this.contactService.get(user, contactId)).build();
    }

    @PutMapping(path = "/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> update(User user, @RequestBody UpdateContactRequest request, @PathVariable(name = "contactId") String contactId){
        request.setId(contactId);
        return WebResponse.<ContactResponse>builder().data(this.contactService.update(user, request)).build();
    }

    @DeleteMapping(path = "/{contactId}")
    public WebResponse<String> delete(User user, @PathVariable(name = "contactId") String contactId) {
        this.contactService.delete(user, contactId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<ContactResponse>> search(User user, 
                                                      @RequestParam(value = "name", required = false) String name,
                                                      @RequestParam(value = "email", required = false) String email, 
                                                      @RequestParam(value = "phone", required = false) String phone, 
                                                      @RequestParam(value = "page",  required = false, defaultValue = "0") Integer page, 
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        SearchContactRequest searchContactRequest = SearchContactRequest.builder()
                         .name(name)
                         .email(email)
                         .phone(phone)
                         .page(page)
                         .size(size)
                         .build();
        Page<ContactResponse> contactResponse = this.contactService.search(user, searchContactRequest);
        System.out.println("TRIGEREDDD");
        return WebResponse.<List<ContactResponse>>builder()
            .data(contactResponse.getContent())
            .paging(
                PagingResponse.builder()
                  .currentPage(contactResponse.getNumber())
                  .totalPage(contactResponse.getTotalPages())
                  .size(contactResponse.getSize())
                  .build())
            .build();
    }

}
