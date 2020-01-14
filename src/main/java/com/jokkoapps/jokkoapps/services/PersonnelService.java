package com.jokkoapps.jokkoapps.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.jokkoapps.jokkoapps.exception.AppException;
import com.jokkoapps.jokkoapps.model.Extension;
import com.jokkoapps.jokkoapps.model.PasswordResetToken;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.payload.NewPersonnelRequest;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.RoleRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;

@Service
@Transactional
public class PersonnelService {
	
    @Autowired
    JokkoMailSender jokkoMailSender;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;
    
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private AgentRepository personnelRepository;
	
    @Autowired
    EslServices eslService;

    public Personnel createPersonnel(com.jokkoapps.jokkoapps.model.Service service, Personnel personnel, boolean load) throws MessagingException, IOException {


    	personnel.setService(service);
    	
    	// Ajout extension du personnel
    	Extension extension = new Extension();
    	extension.setExtension(UUID.randomUUID().toString());
    	extension.setSipPassword(UUID.randomUUID().toString());
    	extension.setExtensionType("AGENT");
    	extension.setAccountCode(UUID.randomUUID().toString());
    	extension.setDisplayName(personnel.getFirstname()+" "+personnel.getLastname());
    	personnel.setExtension(extension);
    	
    	
    	// Création password Personnel
    	int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
    	personnel.setPassword(passwordEncoder.encode(generatedString));
    	
    	// Création roles Personnel
        Role userRole = roleRepository.findByName(RoleName.ROLE_AGENT)
        .orElseThrow(() -> new AppException("User Role not set."));
        
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        personnel.setRoles(userRoles);
    			
    	personnel.setEnabled(true);
    	
    	Personnel agentSave = personnelRepository.save(personnel);
 
    	// Remettre mot de passe clair pour envoi mail
    	personnel.setPassword(generatedString);
    	
    	// Configuration personnel dans serveur freeswitch
    	if(load) {
    		eslService.addNewCallcenter(personnel.getExtension().getExtension(), personnel.getService().getDomaine());
    	}else {
    		eslService.addNewAgent(personnel.getExtension().getExtension(), personnel.getService().getDomaine());
    	}
    	
    	
    	return personnel;
    }
}
