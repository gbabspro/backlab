package com.jokkoapps.jokkoapps.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.model.Manager;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.ServiceType;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.model.Widget;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.NewServiceRequest;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.ManagerRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.repository.WidgetRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.UserPrincipal;
import com.jokkoapps.jokkoapps.services.ContactcenterService;
import com.jokkoapps.jokkoapps.services.EslServices;

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
    UserRepository userRepository;
    
    @Autowired
    ManagerRepository managerRepository;
	
    @Autowired
	private AgentRepository personnelRepository;
	
    @Autowired
    ContactcenterService contactcenterService;
    
    @Autowired
    EslServices eslService;
	
    @PostMapping("/new/service")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> newService(@Valid @RequestBody NewServiceRequest request, @CurrentUser UserPrincipal currentUser) throws MessagingException, IOException {
    
    	Optional<Manager> isManager = managerRepository.findById(currentUser.getId());
    	
		if (isManager.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		Manager manager = isManager.get();
		
        Service service = new Service();
        
		if(request.getServiceType().equalsIgnoreCase("SERVICE_CHAT")) {
            return null;
		}else if(request.getServiceType().equalsIgnoreCase("SERVICE_CALL")) {
			service.setTypeService(ServiceType.SERVICE_CALL);	
		}
        
		service.setManager(manager);
		service.setContactId("CONTACTCENTER_"+UUID.randomUUID().toString());
		service.setDomaine(request.getDomaine());
		service.setEnabled(true);
		
		Service serviceResult = contactcenterService.createContactcenter(service);	
		
    	eslService.reloadService(service.getDomaine());
		
        return ResponseEntity.accepted().body(serviceResult);
    }
    
    
    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasRole('MANAGER') ")
    public ResponseEntity<?> getService(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
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
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
		Widget widget = widgetRepo.findByServiceId(serviceOptional.get().getId()).get();
		
		return ResponseEntity.status(HttpStatus.OK)
    	        .body(widget);
    }
    
    
    
    @GetMapping("/service/operators/list/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getServicePersonnels(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
    	Service service = serviceOptional.get();
        List<Personnel> personnels = personnelRepository.findByServiceId(service.getId());
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(personnels);
    }
	
    
    @GetMapping("/user/services/list")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getUserServices(@CurrentUser UserPrincipal currentUser) {
    	
    	List<Service> listService = serviceRepository.findByManagerId(currentUser.getId());
    	
    	if(listService.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
    	return ResponseEntity.status(HttpStatus.OK)
    	        .body(listService);
    }
    
    
    @GetMapping("/service/list/agent/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity getListAgent(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
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
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
    	Service service = serviceOptional.get();
    	
    	if(!service.isEnabled()) {
    		
    		return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new ApiResponse(false, "Le service est déjà désactivé "));
    	}
    	
    	service.setEnabled(false);
    	
    	serviceRepository.save(service);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Le service a bien été mis à jour !"));
    }
    
    
    @PostMapping("/update/widget/{widgetId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateWidget(@PathVariable Long widgetId, @Valid @RequestBody Widget requestWidget) throws IOException {
    	
    	Optional<Widget> widgetOp = widgetRepo.findById(widgetId);
    	
    	if(widgetOp.isPresent() != true) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
    	Widget widget = widgetOp.get();
    	
    	widget.setBtnBackground(requestWidget.getBtnBackground());
    	widget.setTheme(requestWidget.getTheme());
    	Service service = widget.getService();
		
    	URL url = new URL("http://srv.babacargaye.com/testfile/filegenerator.php?name="+widget.getUrl()+
    			"&sipuserpass="+service.getExtensionUser().getSipPassword()+
    			"&sipuser="+service.getExtensionUser().getExtension()+
    			"&center="+service.getContactId()+"&theme="+widget.getTheme().split("#")[1]);
    	
    	HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
    	cnx.connect();
    	
    	if( cnx.getResponseCode() == HttpURLConnection.HTTP_OK ){
    		
    		BufferedReader input = new BufferedReader(new InputStreamReader(
    				cnx.getInputStream()));

            input.close();
            
    	}else{
    	    
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite");
    	}
    	
    	widgetRepo.save(widget);
    	
    	return ResponseEntity.accepted().body(new ApiResponse(true, "Le widget a bien été mis à jour !"));
    }
    
    
    
    @PostMapping("/service/unlock/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> enableService(@PathVariable Long serviceId) {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
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
