package com.restfull.restfullapi.services;

import org.springframework.data.domain.Page;

import com.restfull.restfullapi.dtos.ContactResponse;
import com.restfull.restfullapi.dtos.CreateContactRequest;
import com.restfull.restfullapi.dtos.SearchContactRequest;
import com.restfull.restfullapi.dtos.UpdateContactRequest;
import com.restfull.restfullapi.entities.User;

public interface ContactService {
    
    public ContactResponse createContact(User user, CreateContactRequest request);

    public ContactResponse get(User user, String contactId);

    public ContactResponse update(User user, UpdateContactRequest request);

    public void delete(User user, String contactId);

    public Page<ContactResponse> search(User user, SearchContactRequest request);
}
