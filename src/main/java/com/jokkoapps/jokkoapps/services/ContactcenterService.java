package com.jokkoapps.jokkoapps.services;

import java.io.IOException;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jokkoapps.jokkoapps.model.Extension;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.ServiceType;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.model.Widget;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.repository.WidgetRepository;

@Service
@Transactional
public class ContactcenterService {
	
    @Autowired
    PersonnelService personnelService;
    
    @Autowired
    ServiceRepository serviceRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    WidgetRepository widgetRepo;
    

    public com.jokkoapps.jokkoapps.model.Service createContactcenter(com.jokkoapps.jokkoapps.model.Service service) throws MessagingException, IOException {
    
		
		Extension extensionUser = new Extension();
		extensionUser.setExtension(UUID.randomUUID().toString());
		extensionUser.setSipPassword(UUID.randomUUID().toString());
		extensionUser.setExtensionType("USER");
		extensionUser.setAccountCode(UUID.randomUUID().toString());
		extensionUser.setDisplayName(service.getManager().getFirstname()+" "+service.getManager().getLastname());
		
		service.setExtensionUser(extensionUser);
		
		com.jokkoapps.jokkoapps.model.Service serviceResponse = serviceRepository.save(service);
		
		Personnel defaultPersonnel = new Personnel();
		
		defaultPersonnel.setUuidPers(UUID.randomUUID().toString());
		defaultPersonnel.setFirstname(service.getManager().getFirstname());
		defaultPersonnel.setLastname(service.getManager().getLastname());
		defaultPersonnel.setDefaultPers(true);
		defaultPersonnel.setEmail(UUID.randomUUID().toString()+"@isdefaultpers.com");

		serviceResponse.setDefaultPersonnel(personnelService.createPersonnel(serviceResponse, defaultPersonnel));
		
		Widget widget = new Widget();
		
		widget.setService(serviceResponse);
		widget.setBtnBackground("#00695C");
		widget.setTheme("#004D40");
		widgetRepo.save(widget);
		
		return serviceResponse;
    }
}
