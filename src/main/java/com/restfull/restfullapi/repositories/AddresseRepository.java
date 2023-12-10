package com.restfull.restfullapi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restfull.restfullapi.entities.Address;
import com.restfull.restfullapi.entities.Contact;

@Repository
public interface AddresseRepository extends JpaRepository<Address, String> {
    
    public Optional<Address> findFirstByContactAndId(Contact contact, String id);

    public List<Address> findAllByContact(Contact contact);
}
