package com.jokkoapps.jokkoapps.services;

import java.io.IOException;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.jokkoapps.jokkoapps.model.User;

@Service
public class JokkoMailSender {

    @Autowired
    private MessageSource messages;
  
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public void sendMailResetPassword(User user, String token)throws MessagingException, IOException {
    	Context ctx = new Context();
        
        
        String resetUrl = "http://localhost:3000/pages/changer-password/" + token;
        String name = user.getFirstname()+" "+user.getLastname();
    	
        ctx.setVariable("name", name);
        ctx.setVariable("resetUrl", resetUrl);
    	

        String recipientAddress = user.getEmail();
        String subject = "Modifier votre votre mot de passe";
         
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setFrom("contact@babacargaye.com");
        
        String htmlContent = this.templateEngine.process("email-reset-password.html", ctx);
        email.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}
