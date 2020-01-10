package com.jokkoapps.jokkoapps.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jokkoapps.jokkoapps.model.PasswordResetToken;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.model.VerificationToken;
import com.jokkoapps.jokkoapps.repository.PasswordResetTokenRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.repository.VerificationTokenRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;

@Service
@Transactional
public class UserService {
	
    @Autowired
    private UserRepository repository;
    
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void saveRegisteredUser(User user) {
    	repository.save(user);
    }
    
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }
    
    public boolean validatePasswordResetToken(String token) {
    	
    	PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
    	
    	if (passToken == null) {
    	        
    		return false;
    	}
    	
    	return true;
    }
    
    
    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
}