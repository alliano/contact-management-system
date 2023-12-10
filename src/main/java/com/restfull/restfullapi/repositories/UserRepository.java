package com.restfull.restfullapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restfull.restfullapi.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    public Optional<User> findFirstByToken(String token);
    
 }
