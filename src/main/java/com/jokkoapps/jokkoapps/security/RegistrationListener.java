package com.jokkoapps.jokkoapps.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring3.SpringTemplateEngine;

import com.jokkoapps.jokkoapps.model.Manager;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.payload.OnRegistrationCompleteEvent;
import com.jokkoapps.jokkoapps.services.ManagerService;
import com.jokkoapps.jokkoapps.services.UserService;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class RegistrationListener implements
  ApplicationListener<OnRegistrationCompleteEvent> {
  
    @Autowired
    private ManagerService managerService;
  
    @Autowired
    private MessageSource messages;
  
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
 
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
			this.confirmRegistration(event);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
    private void confirmRegistration(OnRegistrationCompleteEvent event) throws MessagingException, IOException {
    	
    	Context ctx = new Context();

        Manager manager = event.getManager();
        String token = UUID.randomUUID().toString();
        managerService.createVerificationToken(manager, token);
        
        
        String confirmationUrl = "http://localhost:3000/pages/confirm-registration/" + token;
        String name = manager.getFirstname()+" "+manager.getLastname();
    	
        ctx.setVariable("name", name);
        ctx.setVariable("confirmationUrl", confirmationUrl);
    	

        String recipientAddress = manager.getEmail();
        String subject = "Activation de votre compte chez AlloSky";
         
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setFrom("AlloSky <hello@allosky.net>");
        
        String htmlContent = this.templateEngine.process("email-simple.html", ctx);
        email.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}