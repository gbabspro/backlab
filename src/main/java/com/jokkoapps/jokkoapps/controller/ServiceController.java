package com.jokkoapps.jokkoapps.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.model.Agent;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.UpdateService;
import com.jokkoapps.jokkoapps.payload.UserSummary;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;

@RestController
@RequestMapping("/api")
public class ServiceController {

    
    @Autowired
    ServiceRepository serviceRepository;
    
	@Autowired
	private AgentRepository agentRepository;
    
    @PutMapping("/service/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateService(@PathVariable Long serviceId, @Valid @RequestBody UpdateService updateService) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Service service = serviceOptional.get();
    	
    	service.setServiceName(updateService.getServiceName());
    	service.setOrganisation(updateService.getOrganisation());
    	
    	serviceRepository.save(service);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Le service a bien été mis à jour !"));
    }
    
    @GetMapping("/service/list/agent/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity getListAgent(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	List<Agent> agents = agentRepository.findByServiceId(serviceId);
    		
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(agents);
    }
    
    
    @PostMapping("/service/lock/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> disableService(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Service service = serviceOptional.get();
    	
    	if(!service.isEnabled()) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est déjà désactivé !"),
                    HttpStatus.ALREADY_REPORTED);
    	}
    	
    	service.setEnabled(false);
    	
    	serviceRepository.save(service);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Le service a bien été mis à jour !"));
    }
    
    @PostMapping("/service/unlock/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> enableService(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Service service = serviceOptional.get();
    	
    	if(service.isEnabled()) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est déjà activé !"),
                    HttpStatus.ALREADY_REPORTED);
    	}
    	
    	service.setEnabled(true);
    	
    	serviceRepository.save(service);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Le service a bien été mis à jour !"));
    }
}
