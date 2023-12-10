package com.restfull.restfullapi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.restfull.restfullapi.dtos.ContactResponse;
import com.restfull.restfullapi.dtos.CreateContactRequest;
import com.restfull.restfullapi.dtos.SearchContactRequest;
import com.restfull.restfullapi.dtos.UpdateContactRequest;
import com.restfull.restfullapi.entities.Contact;
import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.ContactRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;

@Service @AllArgsConstructor
public class ContactServiceImpl implements ContactService {
    
    private final ContactRepository contactRepository;

    private final ValidationService validationService;

    @Override @Transactional
    public ContactResponse createContact(User user, CreateContactRequest request) {
        this.validationService.validate(request);
        Contact contact = Contact.builder()
                    .id(UUID.randomUUID().toString())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .user(user)
                    .build();
        this.contactRepository.save(contact);

        return ContactResponse.builder()
            .id(contact.getId())
            .firstName(contact.getFirstName())
            .lastName(contact.getLastName())
            .email(contact.getEmail())
            .phone(contact.getPhone())
            .build();
    }

    @Override @Transactional(readOnly = true)
    public ContactResponse get(User user, String contactId) {
        Contact contact = this.contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
        return ContactResponse.builder()
            .id(contact.getId())
            .firstName(contact.getFirstName())
            .lastName(contact.getLastName())
            .email(contact.getEmail())
            .phone(contact.getPhone())
            .build();
    }

    @Override @Transactional
    public ContactResponse update(User user, UpdateContactRequest request) {
        this.validationService.validate(request);
        Contact contact = this.contactRepository.findFirstByUserAndId(user, request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());

        this.contactRepository.save(contact);
        return ContactResponse.builder()
            .firstName(contact.getFirstName())
            .lastName(contact.getLastName())
            .email(contact.getEmail())
            .phone(contact.getPhone())
            .id(contact.getId())
            .build();
    }

    @Override
    public void delete(User user, String contactId) {
        this.contactRepository.delete(this.contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")));
    }

    @Override @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request) {
        Specification<Contact> specification = (root, query, builder) -> {
            ArrayList<Predicate> prredicates = new ArrayList<>();
            prredicates.add(builder.equal(root.get("user"), user));
            if(Objects.nonNull(request.getName())) {
                prredicates.add(builder.or(
                    builder.like(root.get("firstName"), "%".concat(request.getName()).concat("%")),
                    builder.like(root.get("lastName"), "%".concat(request.getName()).concat("%"))
                ));
            }

            if(Objects.nonNull(request.getEmail())) {
                prredicates.add(
                    builder.like(root.get("email"), "%".concat(request.getEmail()).concat("%"))
                );
            }

            if(Objects.nonNull(request.getPhone())) {
                prredicates.add(
                    builder.like(root.get("phone"), "%".concat(request.getPhone()).concat("%"))
                );
            }
            return query.where(prredicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponseList = contacts.getContent().stream().map(contact -> {
                    return ContactResponse.builder()
                        .id(contact.getId())
                        .firstName(contact.getFirstName())
                        .lastName(contact.getLastName())
                        .email(contact.getEmail())
                        .phone(contact.getPhone())
                        .build();
                }).collect(Collectors.toList());

        return new PageImpl<>(contactResponseList, pageable, contacts.getTotalElements());
    }

}
