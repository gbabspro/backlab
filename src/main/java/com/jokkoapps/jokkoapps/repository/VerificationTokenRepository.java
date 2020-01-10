package com.jokkoapps.jokkoapps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jokkoapps.jokkoapps.model.Manager;
import com.jokkoapps.jokkoapps.model.VerificationToken;

@Repository
public interface VerificationTokenRepository 
extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);

  VerificationToken findByManager(Manager manager);
  
}