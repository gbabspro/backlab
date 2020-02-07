package com.jokkoapps.jokkoapps.services;

import java.util.HashMap;
import java.util.List;

import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.jokkoapps.jokkoapps.model.PasswordResetToken;
import com.jokkoapps.jokkoapps.model.User;

@Service
public class EslServices {

	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	public EslServices() {
	    try {

	        final Client inboudClient = new Client();
	        inboudClient.connect("srv.babacargaye.com", 8021, "ClueCon", 10);
	        
	        		
	        inboudClient.addEventListener(new IEslEventListener() {

				@Override
				public void eventReceived(EslEvent event) {
					
					
					
					HashMap<String, String> agentStatus = new HashMap<String, String>();

					if(event.getEventHeaders().containsKey("CC-Action") && (event.getEventHeaders().get("CC-Action").equals("bridge-agent-end") || event.getEventHeaders().get("CC-Action").equals("bridge-agent-start") || event.getEventHeaders().get("CC-Action").equals("member-queue-start") || event.getEventHeaders().get("CC-Action").equals("member-queue-end"))) {

						agentStatus.put("CCQueue", event.getEventHeaders().get("CC-Queue"));
						agentStatus.put("CCAction", event.getEventHeaders().get("CC-Action"));
						messagingTemplate.convertAndSendToUser(event.getEventHeaders().get("CC-Queue"), "/queue/update", agentStatus);
					}
					
					
				}

				@Override
				public void backgroundJobResultReceived(EslEvent event) {

				}
	        });
	        inboudClient.setEventSubscriptions("plain", "all");

	      } catch (Throwable t) {

	      }
	}
	
    public void dellAgent(String agentId) {
    	
    	String str = "callcenter_config agent del "+agentId+"@51.91.120.241";
    	this.sendApiMsg(str);
    }
    
    public void addNewAgent(String queue) {
    	
    	String strReload = "callcenter_config queue reload "+queue;
    	this.sendApiMsg(strReload);
    }
    
	
    
    public void loadService(String queue) {
    	
    	String str = "callcenter_config queue load "+queue;
    	this.sendApiMsg(str);
    }
    
    public void reloadService(String queue) {
    	
    	String str = "callcenter_config queue load "+queue;
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
