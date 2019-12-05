package com.jokkoapps.jokkoapps.controller;

import com.jokkoapps.jokkoapps.exception.AppException;
import com.jokkoapps.jokkoapps.model.PasswordResetToken;
import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.model.VerificationToken;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.ChangePasswordRequest;
import com.jokkoapps.jokkoapps.payload.JwtAuthenticationResponse;
import com.jokkoapps.jokkoapps.payload.LoginRequest;
import com.jokkoapps.jokkoapps.payload.OnRegistrationCompleteEvent;
import com.jokkoapps.jokkoapps.payload.ResetPasswordRequest;
import com.jokkoapps.jokkoapps.payload.SignUpRequest;
import com.jokkoapps.jokkoapps.repository.PasswordResetTokenRepository;
import com.jokkoapps.jokkoapps.repository.RoleRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.repository.VerificationTokenRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.JwtTokenProvider;
import com.jokkoapps.jokkoapps.services.JokkoMailSender;
import com.jokkoapps.jokkoapps.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;
    
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserService userService;
    
    @Autowired
    JokkoMailSender jokkoMailSender;
    
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    	Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
    	System.out.println("after after after");
    	if(userOptional.isPresent()) {
    		
    		User user = userOptional.get();
    		if(user.getPassword().equalsIgnoreCase("new")) {
    			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(user);
    		}
    	}
    	
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, WebRequest request) {

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getFirstname(), signUpRequest.getLastname(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);
        
        try {
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent
          (result, request.getLocale(), appUrl));
        } catch (Exception me) {
        	System.out.println("message erreur erreur "+me.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/me")
                .buildAndExpand(result.getEmail()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
    
    @GetMapping("/regitrationConfirm")
    public ResponseEntity<?> confirmRegistration
      (WebRequest request, @RequestParam("token") String token) {
      
        Locale locale = request.getLocale();
         
        VerificationToken verificationToken = userService.getVerificationToken(token);
        
        if (verificationToken == null) {
            return new ResponseEntity(new ApiResponse(false, "Ce lien est invalide !"),
                    HttpStatus.BAD_REQUEST);
        }
         
        User user = verificationToken.getUser();
        
        Calendar cal = Calendar.getInstance();
        
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return new ResponseEntity(new ApiResponse(false, "Ce lien est invalide !"),
                    HttpStatus.BAD_REQUEST);
        } 
        
        Role userRole = roleRepository.findByName(RoleName.ROLE_MANAGER)
        .orElseThrow(() -> new AppException("User Role not set."));
        
        Set<Role> userRoles = new HashSet<>();
        
        userRoles.add(userRole);

        user.setRoles(userRoles);

        user.setEnabled(true); 
        userRepository.save(user); 

        return ResponseEntity.accepted().body(new ApiResponse(true, "Votre compte a bien étè activé !"));
    }
    
    
    @GetMapping("/resendRegistrationToken")
    public ResponseEntity<?> resendRegistrationToken(
      HttpServletRequest request, @RequestParam("token") String existingToken) throws MessagingException, IOException {
    	
        VerificationToken vToken = tokenRepository.findByToken(existingToken);
        
        if(vToken == null) {
            return new ResponseEntity(new ApiResponse(false, "Le token n'est pas valide !"),
                    HttpStatus.BAD_REQUEST);
        }
        
        VerificationToken newToken = userService.generateNewVerificationToken(vToken);
         
        User user = userService.getUser(newToken.getToken());
               
		jokkoMailSender.sendMailResetPassword(user, newToken.getToken());
		
		return ResponseEntity.accepted().body(new ApiResponse(true, "Un e-mail de réinitialisation a été envoyé sur votre adresse e-mail : "+user.getEmail()));
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<?>  resetPassword(@Valid @RequestBody ResetPasswordRequest passwordRequest) throws MessagingException, IOException {
    	
    	Optional<User> opntionalUser = userRepository.findByEmail(passwordRequest.getEmail());
		
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "l'e-mail ne correspond à aucun compte !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		User user = opntionalUser.get();
		
		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);
		
		jokkoMailSender.sendMailResetPassword(user, token);
		
		return ResponseEntity.accepted().body(new ApiResponse(true, "Un e-mail de réinitialisation a été envoyé sur votre adresse e-mail : "+user.getEmail()));
		
	}
    
    
    @PostMapping("/changePassword")
    public ResponseEntity<?> showChangePasswordPage(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        
        PasswordResetToken passToken = passwordTokenRepository.findByToken(changePasswordRequest.getToken());
        
        if (passToken == null) {
            return new ResponseEntity(new ApiResponse(false, "Le token n'est pas valide !"),
                    HttpStatus.BAD_REQUEST);
        }
        
        User user = passToken.getUser();
        
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));

        User result = userRepository.save(user);
        
		return ResponseEntity.accepted().body(new ApiResponse(true, "Votre mot de passe a ben étè modifié !"));

    }
    
}