package com.jokkoapps.jokkoapps.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.Widget;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.WidgetRepository;

@RestController
@RequestMapping("/api")
public class WidgetController {
	
	@Autowired
    WidgetRepository widgetRepo;
	
    @Autowired
    ServiceRepository serviceRepository;

    @GetMapping("/widget/generate/url/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> widgetUrlGenerator(@PathVariable Long serviceId) throws Exception {
    	
    	Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
    	Service service = serviceOptional.get();
    	String nameUrl = RandomStringUtils.randomAlphabetic(15)+".js";
    	
    	URL url = new URL("http://srv.babacargaye.com/testfile/filegenerator.php?name="+nameUrl+
    			"&sipuserpass="+service.getExtensionUser().getSipPassword()+
    			"&sipuser="+service.getExtensionUser().getExtension()+
    			"&center="+service.getContactId()+"&theme=004D40");
    	
    	HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
    	cnx.connect();
    	
    	if( cnx.getResponseCode() == HttpURLConnection.HTTP_OK ){
    		
    		BufferedReader input = new BufferedReader(new InputStreamReader(
    				cnx.getInputStream()));

    		
        	Optional<Widget> OptionalWidget = widgetRepo.findByServiceId(service.getId());
        	if(OptionalWidget.isPresent()){
        		
        		Widget widget = OptionalWidget.get();
        		widget.setUrl(nameUrl); 
        		widgetRepo.save(widget);
        	}
        	
            input.close();
            
    	}else{
    	    
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite");
    	}

        
        return ResponseEntity.accepted().body(new ApiResponse(true, "L'opération a bien étè effectuée"));
    }
    
    @GetMapping("/has/widget/url/{serviceId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> verifHasUrl(@PathVariable Long serviceId) {
    	
        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
    	
    	if(serviceOptional.isPresent() != true) {    		
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucun service"));
    	}
    	
    	Service service = serviceOptional.get();
    	Optional<Widget> OptionalWidget = widgetRepo.findByServiceId(service.getId());
    	if(OptionalWidget.isPresent()){
    		
    		String urlWidget = OptionalWidget.get().getUrl();
    		
    		if(urlWidget == null || urlWidget.isEmpty()) {
    			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucune url trouvée"));
    		}
    		
    		return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "L'url existe"));
    	}
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Aucune url trouvée"));
    	
    }
}
