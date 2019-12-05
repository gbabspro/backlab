package com.jokkoapps.jokkoapps.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.exception.AppException;
import com.jokkoapps.jokkoapps.model.Agent;
import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.NewAgentRequest;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.RoleRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;

@RestController
@RequestMapping("/api")
public class AgentController {
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private AgentRepository agentRepository;
	
    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/agent/{serviceId}/new")
    public ResponseEntity<?> createAgent(@PathVariable (value = "serviceId") Long serviceId,
                                 @Valid @RequestBody Agent agent) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	if(serviceOptional.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Service service = serviceOptional.get();
    	agent.setService(service);
    	
        Role userRole = roleRepository.findByName(RoleName.ROLE_AGENT)
        .orElseThrow(() -> new AppException("User Role not set."));
        
        Set<Role> userRoles = new HashSet<>();
        
        userRoles.add(userRole);
    			
    	agent.setRoles(userRoles);
    	agent.setEnabled(false);
    	agent.setPassword("new");
    	agentRepository.save(agent);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Un agent a bien étè ajouté !"));
    }
}
