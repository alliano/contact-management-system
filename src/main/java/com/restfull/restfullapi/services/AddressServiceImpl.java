package com.restfull.restfullapi.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.restfull.restfullapi.dtos.AddressResponse;
import com.restfull.restfullapi.dtos.CreateAddressRequest;
import com.restfull.restfullapi.dtos.UpdateAddressRequest;
import com.restfull.restfullapi.entities.Address;
import com.restfull.restfullapi.entities.Contact;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.AddresseRepository;
import com.restfull.restfullapi.repositories.ContactRepository;
import lombok.AllArgsConstructor;

@Service @AllArgsConstructor
public class AddressServiceImpl implements AddresService {

    private final AddresseRepository addresseRepository;

    private final ValidationService validationService;

    private final ContactRepository contactRepository;

    @Override @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {
        this.validationService.validate(request);
        Contact contact = this.contactRepository.findFirstByUserAndId(user , request.getContactId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
        
        Address address = Address.builder()
                .id(UUID.randomUUID().toString())
                .contact(contact) 
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .province(request.getProvince())
                .street(request.getStreet())
                .build();
        this.addresseRepository.save(address);
        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
            .id(address.getId())
            .city(address.getCity())
            .country(address.getCountry())
            .postalCode(address.getPostalCode())
            .street(address.getStreet())
            .province(address.getProvince())
            .build();
    }

    @Override @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId) {
        Contact contact = this.contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
        Address address = this.addresseRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        return toAddressResponse(address);
    }

    @Override @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request) {
        this.validationService.validate(request);
        Contact contact = this.contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
        Address address = this.addresseRepository.findFirstByContactAndId(contact, request.getAddressId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setStreet(request.getStreet());
        this.addresseRepository.save(address);
        return toAddressResponse(address);
    }
    
    @Override
    public void delete(User user, String contactId, String addressId) {
        Contact contact = this.contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
        Address address = this.addresseRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        this.addresseRepository.delete(address);
    }
    
    @Override
    public List<AddressResponse> list(User user, String contactId) {
        Contact contact = this.contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
        return this.addresseRepository.findAllByContact(contact).stream().map(address -> {
            return toAddressResponse(address);
        }).collect(Collectors.toList());
    }
}
