package com.jokkoapps.jokkoapps.controller;

import java.util.List;
import java.util.Optional;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class EslController {
	
    @GetMapping("/operator/login")
    @PreAuthorize("hasRole('AGENT') or hasRole('MANAGER')")
    public ResponseEntity<?> setLogin(@PathVariable (value = "userId") Long userId) {
    	
    	List<String> response = this.sendApiMsg("callcenter_config agent set status "+userId+" 'Logged Out'");
    	
    	for(String str : response) {
    	System.out.println(str);
    	}
    	
    	return ResponseEntity.accepted().body(response);
    }

	
	private List<String> sendApiMsg(String msg) {
	    try {

	        final Client inboudClient = new Client();
	        inboudClient.connect("srv.babacargaye.com", 8021, "ClueCon", 10);
	        
	        EslMessage response = inboudClient.sendSyncApiCommand(msg, "" );
	        
	        
	         inboudClient.close();
	         
	         return response.getBodyLines();

	      } catch (Throwable t) {
	       
	      }
	    
	    return null;
	}
}
