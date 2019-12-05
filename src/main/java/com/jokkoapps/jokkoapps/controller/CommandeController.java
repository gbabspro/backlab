package com.jokkoapps.jokkoapps.controller;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.model.BouttonAppelConf;
import com.jokkoapps.jokkoapps.model.Commande;
import com.jokkoapps.jokkoapps.model.Offre;
import com.jokkoapps.jokkoapps.model.OffreName;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.ServiceType;
import com.jokkoapps.jokkoapps.model.StatusName;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.CommandeServiceRequest;
import com.jokkoapps.jokkoapps.payload.UserSummary;
import com.jokkoapps.jokkoapps.repository.BtnRepository;
import com.jokkoapps.jokkoapps.repository.CommandeRepository;
import com.jokkoapps.jokkoapps.repository.OffreRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;
import com.jokkoapps.jokkoapps.services.UserService;

@RestController
@RequestMapping("/api")
public class CommandeController {
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;
    
    @Autowired
    BtnRepository btnRepository;
    
    @Autowired
    ServiceRepository serviceRepository;
    
    @Autowired
    OffreRepository offreRepository;
    
    @Autowired
    CommandeRepository commandeRepository;
    
    @PostMapping("/commande/new/service")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> commandeService(@Valid @RequestBody CommandeServiceRequest commandeRequest, @CurrentUser UserPrincipal currentUser) {
    
    	Optional<User> opntionalUser = userRepository.findById(currentUser.getId());
    	
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		User user = opntionalUser.get();
		
		Service service = new Service();
		
		if(commandeRequest.getServiceType().equalsIgnoreCase("SERVICE_CHAT")) {
			service.setTypeService(ServiceType.SERVICE_CHAT);	
		}else if(commandeRequest.getServiceType().equalsIgnoreCase("SERVICE_CALL")) {
			service.setTypeService(ServiceType.SERVICE_CALL);	
		}
		
		service.setOrganisation(commandeRequest.getOrganisation());
		service.setServiceName(commandeRequest.getServiceName());
		service.setUser(user);
		service.setContactId(UUID.randomUUID()
            .toString());
		service.setEnabled(true);
		
		Offre offre = new Offre();
		
		if(commandeRequest.getOffreName().equalsIgnoreCase("OFFRE_START")) {
			offre = offreRepository.findByName(OffreName.OFFRE_START);
		}else if(commandeRequest.getOffreName().equalsIgnoreCase("OFFRE_PREMIUM")) {
			offre = offreRepository.findByName(OffreName.OFFRE_PREMIUM);
		}else if(commandeRequest.getOffreName().equalsIgnoreCase("OFFRE_BUSINESS")) {
			offre = offreRepository.findByName(OffreName.OFFRE_BUSINESS);
		}else {
            return new ResponseEntity(new ApiResponse(false, "Merci de sélectionner une offre valide !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		Commande commande = new Commande();
		
		service.setOffre(offre);
		
		commande.setUser(user);
		commande.setService(service);
		commande.setStatus(StatusName.STATUS_DONE);
		
		BouttonAppelConf btn = new BouttonAppelConf();
		btn.setBackground("14631C");
		btn.setColor("fff");
		btn.setContent("Contacter nous");
		
		btn.setService(service);
		
		Service serviceResponse = serviceRepository.save(service);
		btnRepository.save(btn);
		commandeRepository.save(commande);
		
        return ResponseEntity.accepted().body(serviceResponse);
    }
    
    @GetMapping("/service/list")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Service> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        
        List<Service> services = serviceRepository.findByUserId(currentUser.getId());
        
        return services;
    }
    
    @GetMapping("/commandes/list")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Commande> getCommandeList(@CurrentUser UserPrincipal currentUser) {
        
        List<Commande> commandes = commandeRepository.findByUserId(currentUser.getId());
        
        return commandes;
    }
}
