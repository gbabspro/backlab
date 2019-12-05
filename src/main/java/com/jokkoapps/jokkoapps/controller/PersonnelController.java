package com.jokkoapps.jokkoapps.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.exception.AppException;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.NewPersonnelRequest;
import com.jokkoapps.jokkoapps.payload.UpdatePersonnelProfile;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.RoleRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;

@RestController
@RequestMapping("/api")
public class PersonnelController {
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private AgentRepository personnelRepository;
	
    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/personnel/{serviceId}/new")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createAgent(@PathVariable (value = "serviceId") Long serviceId,
                                 @Valid @RequestBody NewPersonnelRequest newPersonnelRequest) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
        if(personnelRepository.existsByEmail(newPersonnelRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use !"),
                    HttpStatus.CONFLICT);
        }
    	
    	Personnel personnel = new Personnel();
    	
    	Service service = serviceOptional.get();
    	personnel.setService(service);
    	personnel.setFirstname(newPersonnelRequest.getFirstname());
    	personnel.setLastname(newPersonnelRequest.getLastname());
    	personnel.setEmail(newPersonnelRequest.getEmail());
    	personnel.setPassword(UUID.randomUUID().toString());
    	
    	if(newPersonnelRequest.getRole().equalsIgnoreCase("ROLE_AGENT")) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_AGENT)
            .orElseThrow(() -> new AppException("User Role not set."));
            
            Set<Role> userRoles = new HashSet<>();
            
            userRoles.add(userRole);
            personnel.setRoles(userRoles);
    	}else if(newPersonnelRequest.getRole().equalsIgnoreCase("ROLE_SUP")){
            Role userRole = roleRepository.findByName(RoleName.ROLE_SUPERVISEUR)
            .orElseThrow(() -> new AppException("User Role not set."));
            
            Set<Role> userRoles = new HashSet<>();
            
            userRoles.add(userRole);
            personnel.setRoles(userRoles);
    	}
    			
    	
    	personnel.setEnabled(true);
    	Personnel agentSave = personnelRepository.save(personnel);
    	
    	return ResponseEntity.accepted().body(agentSave);
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
}
