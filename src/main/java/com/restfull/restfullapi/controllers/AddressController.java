package com.restfull.restfullapi.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restfull.restfullapi.dtos.AddressResponse;
import com.restfull.restfullapi.dtos.CreateAddressRequest;
import com.restfull.restfullapi.dtos.UpdateAddressRequest;
import com.restfull.restfullapi.dtos.WebResponse;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.services.AddresService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController @RequestMapping(path = "/api/contacts/")
public class AddressController {
    
    private final AddresService addresService;

    @PostMapping(path = "/{contactId}/addresses")
    public WebResponse<AddressResponse> create(User user,@RequestBody CreateAddressRequest request, @PathVariable(value = "contactId") String contactId) {
        request.setContactId(contactId);
        return WebResponse.<AddressResponse>builder().data(this.addresService.create(user, request)).build();
    }

    @GetMapping(path = "/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> get(User user, @PathVariable(value = "contactId") String contactId, @PathVariable(value = "addressId") String addressId) {
        return WebResponse.<AddressResponse>builder().data(this.addresService.get(user, contactId, addressId)).build();
    }

    @PutMapping(path = "/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> update(User user, @RequestBody UpdateAddressRequest request, @PathVariable(value = "contactId") String contactId, @PathVariable(value = "addressId") String addressId) {
        request.setAddressId(addressId);
        request.setContactId(contactId);
        return WebResponse.<AddressResponse>builder().data(this.addresService.update(user, request)).build();
    }

    @DeleteMapping(path = "/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(User user, @PathVariable(value = "contactId") String contactId, @PathVariable(value = "addressId") String addressId) {
        this.addresService.delete(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/{contactId}/addresses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<AddressResponse>> list(User user, @PathVariable(value = "contactId") String contactId) {
        return WebResponse.<List<AddressResponse>>builder().data(this.addresService.list(user, contactId)).build();
    }
}
