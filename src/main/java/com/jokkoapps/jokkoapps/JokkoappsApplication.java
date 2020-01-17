package com.jokkoapps.jokkoapps;

import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.repository.RoleRepository;

import javax.annotation.PostConstruct;

import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackageClasses = { 
		JokkoappsApplication.class,
		Jsr310JpaConverters.class 
})
public class JokkoappsApplication implements CommandLineRunner{
	
	private final RoleRepository roleRepository;
	
	 
    public JokkoappsApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(JokkoappsApplication.class, args);

	    try {

	        final Client inboudClient = new Client();
	        inboudClient.connect("srv.babacargaye.com", 8021, "ClueCon", 10);

	        
	        inboudClient.addEventListener(new IEslEventListener() {

				@Override
				public void eventReceived(EslEvent event) {
					
					if(event.getEventHeaders().containsKey("CC-Action") && event.getEventHeaders().get("CC-Action").equals("agent-status-change")) {
						System.out.println("CC-Agent "+ event.getEventHeaders().get("CC-Agent"));
						System.out.println("CC-Agent-Status "+ event.getEventHeaders().get("CC-Agent-Status"));
						System.out.println("Event-Name "+ event.getEventHeaders().get("Event-Name"));
						System.out.println("event.getEventHeaders() "+ event.getEventHeaders());
						
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

    @Override
    public void run(String... args) throws Exception {
        Long roles = roleRepository.count();

        if (roles <= 0) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
            roleRepository.save(new Role(RoleName.ROLE_AGENT));
            roleRepository.save(new Role(RoleName.ROLE_MANAGER));
        }
    }
}
