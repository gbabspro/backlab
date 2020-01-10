package com.jokkoapps.jokkoapps.controller;

import java.util.HashMap;
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
	
    @GetMapping("/operator/login/{userId}")
    @PreAuthorize("hasRole('AGENT') or hasRole('MANAGER')")
    public ResponseEntity<?> setLogin(@PathVariable (value = "userId") String userId) {
    	
    	List<String> response = this.sendApiMsg("callcenter_config agent set status "+userId+"@51.91.120.241 'Available'");
    	
    	for(String str : response) {
    	System.out.println(str);
    	}
    	
    	return ResponseEntity.accepted().body(response);
    }
    
    
    @GetMapping("/operator/list/{domaine}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getOperatorsList(@PathVariable (value = "domaine") String domaine) {
    	
    	//List<String> response = this.sendApiMsg("callcenter_config queue list agents "+domaine);
    	
    	String response[] = {
    	        "name|instance_id|uuid|type|contact|status|state|max_no_answer|wrap_up_time|reject_delay_time|busy_delay_time|no_answer_delay_time|last_bridge_start|last_bridge_end|last_offered_call|last_status_change|no_answer_count|calls_answered|talk_time|ready_time|external_calls_count",
        	    "40e52e34-7f6b-464a-900d-70e53f4b8918@51.91.120.241|single_box||callback|[leg_timeout=10]user/40e52e34-7f6b-464a-900d-70e53f4b8918@51.91.120.241|Available|Waiting|3|10|10|60|0|0|0|0|1578498038|0|0|0|0|0",
        	    "77e38532-4aca-4e7f-92d8-41e908487d92@51.91.120.241|single_box||callback|[leg_timeout=10]user/77e38532-4aca-4e7f-92d8-41e908487d92@51.91.120.241|Available|Waiting|3|10|10|60|0|0|0|0|1578496170|0|0|0|0|0",
        	    "82c05af0-dee4-4aa7-97ba-b8bbce8ceb27@51.91.120.241|single_box||callback|[leg_timeout=10]user/82c05af0-dee4-4aa7-97ba-b8bbce8ceb27@51.91.120.241|Available|Waiting|3|10|10|60|0|0|0|0|1578576729|0|0|0|0|0",
        	    "fc33bc7b-c8c9-4f80-b4c9-7ce3aad3ca27@51.91.120.241|single_box||callback|[leg_timeout=10]user/fc33bc7b-c8c9-4f80-b4c9-7ce3aad3ca27@51.91.120.241|Available|Waiting|3|10|10|60|0|0|0|0|1578577258|0|0|0|0|0",
        	    "c08e8925-329c-4739-b222-46b4daaac98c@51.91.120.241|single_box||callback|[leg_timeout=10]user/c08e8925-329c-4739-b222-46b4daaac98c@51.91.120.241|Available|Waiting|3|10|10|60|0|0|0|0|1578578059|0|0|0|0|0",
        	    "+OK"};
    	
    	HashMap<String, String> header = new HashMap<String, String>();
    	
    	System.out.println(" response[0] "+response[0]);
    	String arrayHeader[] = response[0].split("\\|");
    	for(String str : arrayHeader) {
    		
    		if(str.equalsIgnoreCase("name") || str.equalsIgnoreCase("status") || str.equalsIgnoreCase("state")) {
    			header.put(str, "");
    		}
    	}
    	 
        	

    	return ResponseEntity.accepted().body(header);
    }
    
    @GetMapping("/operator/logout/{userId}")
    @PreAuthorize("hasRole('AGENT') or hasRole('MANAGER')")
    public ResponseEntity<?> setLogout(@PathVariable (value = "userId") String userId) {
    	
    	List<String> response = this.sendApiMsg("callcenter_config agent set status "+userId+"@51.91.120.241 'Logged Out'");
    	
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
