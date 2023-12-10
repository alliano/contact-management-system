package com.restfull.restfullapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.restfull.restfullapi.entities.Contact;
import com.restfull.restfullapi.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {
    
    public Optional<Contact> findFirstByUserAndId(User user, String contactId);
}
