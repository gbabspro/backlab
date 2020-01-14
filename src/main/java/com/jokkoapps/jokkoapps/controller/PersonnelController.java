package com.jokkoapps.jokkoapps.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.exception.AppException;
import com.jokkoapps.jokkoapps.model.Extension;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.NewPersonnelRequest;
import com.jokkoapps.jokkoapps.payload.UpdatePersonnelProfile;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.RoleRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;
import com.jokkoapps.jokkoapps.services.EslServices;
import com.jokkoapps.jokkoapps.services.JokkoMailSender;
import com.jokkoapps.jokkoapps.services.PersonnelService;

@RestController
@RequestMapping("/api")
public class PersonnelController {
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private AgentRepository personnelRepository;
	
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PersonnelService personnelService;
    
    @Autowired
    EslServices eslService;
    
    @Autowired
    JokkoMailSender jokkoMailSender;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/personnel/{serviceId}/new")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createAgent(@PathVariable (value = "serviceId") Long serviceId,
                                 @Valid @RequestBody NewPersonnelRequest newPersonnelRequest, @CurrentUser UserPrincipal currentUser) throws MessagingException, IOException {
        
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
        if(userRepository.existsByEmail(newPersonnelRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use !"),
                    HttpStatus.CONFLICT);
        }
        
    	Optional<User> opntionalUser = userRepository.findById(currentUser.getId());
    	
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
    	Personnel personnel = new Personnel();

    	personnel.setUuidPers(UUID.randomUUID().toString());
    	personnel.setFirstname(newPersonnelRequest.getFirstname());
    	personnel.setLastname(newPersonnelRequest.getLastname());
    	personnel.setEmail(newPersonnelRequest.getEmail());
		
    	Personnel newPersonnel = personnelService.createPersonnel(serviceOptional.get(), personnel);
    	
		// Configuration personnel dans serveur freeswitch
    	eslService.loadService(newPersonnel.getService().getDomaine());
    	
    	
    	jokkoMailSender.sendMailNewAgent(newPersonnel);
    			
    	return ResponseEntity.accepted().body(newPersonnel);
    }
    
    

    
    
    @PostMapping("/personnel/updateProfil/{persId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changePersonnelProfile(@PathVariable (value = "persId") Long persId, @Valid @RequestBody UpdatePersonnelProfile updatePersonnelProfile) {

    	Optional<Personnel> opntionalUser = personnelRepository.findById(persId);
		
		Personnel personnel = opntionalUser.get();
		
		personnel.setFirstname(updatePersonnelProfile.getFirstname());
		personnel.setLastname(updatePersonnelProfile.getLastname());
		personnel.setEmail(updatePersonnelProfile.getEmail());
        
		personnelRepository.save(personnel);
        
        return ResponseEntity.accepted().body(new ApiResponse(true, "Le profile a bien étè modifié !"));
    }
    
    @PutMapping("/personnel/lock/{persId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> lockPersonnel(@PathVariable Long persId) {
    	
    	Optional<Personnel> agentOptional = personnelRepository.findById(persId);
    	
    	if(agentOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Personnel personnel = agentOptional.get();
    	
    	if(!personnel.isEnabled()) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur déjà inactif !"),
                    HttpStatus.ALREADY_REPORTED);
    	}
    	
    	personnel.setEnabled(false);
    	
    	personnelRepository.save(personnel);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Compte utilisateur désactivé !"));
    }
    
    @PutMapping("/personnel/unlock/{persId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> unlockPersonnel(@PathVariable Long persId) {
    	
    	Optional<Personnel> agentOptional = personnelRepository.findById(persId);
    	
    	if(agentOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Personnel personnel = agentOptional.get();
    	
    	if(personnel.isEnabled()) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur déjà actif !"),
                    HttpStatus.ALREADY_REPORTED);
    	}
    	
    	personnel.setEnabled(true);
    	
    	personnelRepository.save(personnel);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Compte utilisateur activé !"));
    }
    
    @DeleteMapping("/personnel/delete/{persId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deletePersonnel(@PathVariable Long persId) {
    	
    	Optional<Personnel> persOptional = personnelRepository.findById(persId);
    	
    	if(persOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Personnel personnel = persOptional.get();
    	
    	personnelRepository.delete(personnel);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Compte utilisateur supprimé !"));
    }
    
    
    @DeleteMapping("/personnel/service/")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getPersonnel(@PathVariable Long persId) {
    	
    	Optional<Personnel> persOptional = personnelRepository.findById(persId);
    	
    	if(persOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Personnel personnel = persOptional.get();
    	
    	personnelRepository.delete(personnel);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Compte utilisateur supprimé !"));
    }
    
    
    @GetMapping("/operator/me")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> getServiceByOperatorId(@CurrentUser UserPrincipal currentUser) {
    	
    	Optional<Personnel> persOptional = personnelRepository.findById(currentUser.getId());
    	
    	if(persOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Utilisateur introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Personnel personnel = persOptional.get();
    	return ResponseEntity.accepted().body(personnel);
    }
}
