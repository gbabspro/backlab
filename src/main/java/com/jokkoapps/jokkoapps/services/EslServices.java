package com.jokkoapps.jokkoapps.services;

import java.util.List;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.springframework.stereotype.Service;

import com.jokkoapps.jokkoapps.model.PasswordResetToken;
import com.jokkoapps.jokkoapps.model.User;

@Service
public class EslServices {

	
    public void dellAgent(String agentId) {
    	
    	String str = "callcenter_config agent del "+agentId+"@51.91.120.241";
    	this.sendApiMsg(str);
    }
    
    public void addNewAgent(String agentId, String queue) {
    	
    	String strReload = "callcenter_config queue reload "+queue;
    	this.sendApiMsg(strReload);
    	
    	String str = "callcenter_config tier add "+queue+" "+agentId+"@51.91.120.241";
    	this.sendApiMsg(str);
    }
	
    
    public void reloadService(String queue) {
    	
    	String str = "callcenter_config queue reload "+queue;
    	this.sendApiMsg(str);
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
