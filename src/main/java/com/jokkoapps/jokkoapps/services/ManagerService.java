package com.jokkoapps.jokkoapps.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jokkoapps.jokkoapps.model.Manager;
import com.jokkoapps.jokkoapps.model.VerificationToken;
import com.jokkoapps.jokkoapps.repository.ManagerRepository;
import com.jokkoapps.jokkoapps.repository.VerificationTokenRepository;

@Service
@Transactional
public class ManagerService {

    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @Autowired
    private ManagerRepository managerRepository;
    
    
    public Manager getManagerByToken(String verificationToken) {
        Manager manager = tokenRepository.findByToken(verificationToken).getManager();
        return manager;
    }
    
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }
     
    public void createVerificationToken(Manager manager, String token) {
    	System.out.println("Call create token service");
        VerificationToken myToken = new VerificationToken(token, manager);
        tokenRepository.save(myToken);
    }
    
    public VerificationToken generateNewVerificationToken(VerificationToken vToken) {
        vToken.setToken(UUID.randomUUID()
            .toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }
}
