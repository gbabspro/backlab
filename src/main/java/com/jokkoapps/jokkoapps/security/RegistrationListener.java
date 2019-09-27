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

import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.payload.OnRegistrationCompleteEvent;
import com.jokkoapps.jokkoapps.services.UserService;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class RegistrationListener implements
  ApplicationListener<OnRegistrationCompleteEvent> {
  
    @Autowired
    private UserService service;
  
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

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        
        
        String confirmationUrl = "http://localhost:5000/api/auth/regitrationConfirm?token=" + token;
        String name = user.getFirstname()+" "+user.getLastname();
    	
        ctx.setVariable("name", name);
        ctx.setVariable("confirmationUrl", confirmationUrl);
    	

        String recipientAddress = user.getEmail();
        String subject = "Activez votre compte";
         
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setFrom("contact@babacargaye.com");
        
        String htmlContent = this.templateEngine.process("email-simple.html", ctx);
        email.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}