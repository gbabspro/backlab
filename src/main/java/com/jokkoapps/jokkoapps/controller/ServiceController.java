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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.model.BouttonAppelConf;
import com.jokkoapps.jokkoapps.model.Commande;
import com.jokkoapps.jokkoapps.model.Extension;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.ServiceType;
import com.jokkoapps.jokkoapps.model.StatusName;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.model.Widget;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.CommandeServiceRequest;
import com.jokkoapps.jokkoapps.payload.NewServiceRequest;
import com.jokkoapps.jokkoapps.payload.UpdatePersonnelProfile;
import com.jokkoapps.jokkoapps.payload.UpdateService;
import com.jokkoapps.jokkoapps.payload.UserSummary;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.BtnRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.repository.WidgetRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;

@RestController
@RequestMapping("/api")
public class ServiceController {

    
    @Autowired
    ServiceRepository serviceRepository;
    
	@Autowired
	private AgentRepository agentRepository;
	
    @Autowired
    WidgetRepository widgetRepo;
	
	@Autowired
	BtnRepository btnRepository;
	
    @Autowired
    UserRepository userRepository;
	
    @Autowired
	private AgentRepository personnelRepository;
	
	
    @PostMapping("/new/service")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> newService(@Valid @RequestBody NewServiceRequest request, @CurrentUser UserPrincipal currentUser) {
    
    	Optional<User> opntionalUser = userRepository.findById(currentUser.getId());
    	
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		User user = opntionalUser.get();
		
		Service service = new Service();
		
		if(request.getServiceType().equalsIgnoreCase("SERVICE_CHAT")) {
			service.setTypeService(ServiceType.SERVICE_CHAT);	
		}else if(request.getServiceType().equalsIgnoreCase("SERVICE_CALL")) {
			service.setTypeService(ServiceType.SERVICE_CALL);	
		}
		
		service.setUser(user);
		service.setContactId("CONTACTCENTER_"+UUID.randomUUID()
            .toString());
		service.setDomaine(request.getDomaine());
		service.setEnabled(true);
		
		Extension defaultextension = new Extension();
		defaultextension.setExtension(UUID.randomUUID()
            .toString());
		defaultextension.setSipPassword(UUID.randomUUID()
            .toString());
		
		service.setDefaultextension(defaultextension);
		
		Service serviceResponse = serviceRepository.save(service);
		
		Widget widget = new Widget();
		
		widget.setService(serviceResponse);
		widget.setBtnBackground("#00695C");
		widget.setTheme("#004D40");
		widgetRepo.save(widget);
	
		
        return ResponseEntity.accepted().body(serviceResponse);
    }
    
    
    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasRole('MANAGER') ")
    public ResponseEntity<?> getService(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Service service = serviceOptional.get();
    
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(service);
    }
    
    
    @GetMapping("/service/widget/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getServiceWidget(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
		Widget widget = widgetRepo.findByServiceId(serviceOptional.get().getId());
		
		return ResponseEntity.status(HttpStatus.OK)
    	        .body(widget);
    }
    
    
    
    @GetMapping("/service/operators/list/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getServicePersonnels(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Service service = serviceOptional.get();
        List<Personnel> personnels = personnelRepository.findByServiceId(service.getId());
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(personnels);
    }
	
    
    @GetMapping("/user/services/list")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getUserServices(@CurrentUser UserPrincipal currentUser) {
    	
    	List<Service> listService = serviceRepository.findByUserId(currentUser.getId());
    	
    	if(listService.isEmpty()) {
    		return new ResponseEntity(new ApiResponse(false, "Aucun service trouvé !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(listService);
    }
    
    
    @GetMapping("/service/list/agent/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity getListAgent(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	List<Personnel> personnels = agentRepository.findByServiceId(serviceId);
    		
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(personnels);
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
    
    
    @PostMapping("/update/widget/{widgetId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateWidget(@PathVariable Long widgetId, @Valid @RequestBody Widget requestWidget) {
    	
    	Optional<Widget> widgetOp = widgetRepo.findById(widgetId);
    	
    	if(widgetOp.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	Widget widget = widgetOp.get();
    	
    	widget.setBtnBackground(requestWidget.getBtnBackground());
    	widget.setTheme(requestWidget.getTheme());
    	
    	widgetRepo.save(widget);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Le widget a bien été mis à jour !"));
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
    
    @GetMapping("/service/btnconf/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity getBtnConf(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return new ResponseEntity(new ApiResponse(false, "Le service est introuvable !"),
                    HttpStatus.NOT_FOUND);
    	}
    	
    	 BouttonAppelConf btnConf = btnRepository.findByServiceId(serviceId);
    		
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(btnConf);
    }
    
    
}
