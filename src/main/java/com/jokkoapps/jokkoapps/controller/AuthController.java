package com.jokkoapps.jokkoapps.controller;

import com.jokkoapps.jokkoapps.exception.AppException;
import com.jokkoapps.jokkoapps.model.Extension;
import com.jokkoapps.jokkoapps.model.Manager;
import com.jokkoapps.jokkoapps.model.PasswordResetToken;
import com.jokkoapps.jokkoapps.model.Personnel;
import com.jokkoapps.jokkoapps.model.Role;
import com.jokkoapps.jokkoapps.model.RoleName;
import com.jokkoapps.jokkoapps.model.Service;
import com.jokkoapps.jokkoapps.model.ServiceType;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.model.VerificationToken;
import com.jokkoapps.jokkoapps.model.Widget;
import com.jokkoapps.jokkoapps.payload.ApiResponse;
import com.jokkoapps.jokkoapps.payload.ChangePasswordRequest;
import com.jokkoapps.jokkoapps.payload.JwtAuthenticationResponse;
import com.jokkoapps.jokkoapps.payload.LoginRequest;
import com.jokkoapps.jokkoapps.payload.OnRegistrationCompleteEvent;
import com.jokkoapps.jokkoapps.payload.ResetPasswordRequest;
import com.jokkoapps.jokkoapps.payload.SignUpRequest;
import com.jokkoapps.jokkoapps.payload.UserIdentityAvailability;
import com.jokkoapps.jokkoapps.repository.AgentRepository;
import com.jokkoapps.jokkoapps.repository.ManagerRepository;
import com.jokkoapps.jokkoapps.repository.PasswordResetTokenRepository;
import com.jokkoapps.jokkoapps.repository.RoleRepository;
import com.jokkoapps.jokkoapps.repository.ServiceRepository;
import com.jokkoapps.jokkoapps.repository.UserRepository;
import com.jokkoapps.jokkoapps.repository.VerificationTokenRepository;
import com.jokkoapps.jokkoapps.repository.WidgetRepository;
import com.jokkoapps.jokkoapps.security.CurrentUser;
import com.jokkoapps.jokkoapps.security.JwtTokenProvider;
import com.jokkoapps.jokkoapps.services.ContactcenterService;
import com.jokkoapps.jokkoapps.services.EslServices;
import com.jokkoapps.jokkoapps.services.JokkoMailSender;
import com.jokkoapps.jokkoapps.services.ManagerService;
import com.jokkoapps.jokkoapps.services.PersonnelService;
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
import org.springframework.web.bind.annotation.PathVariable;
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
    ManagerRepository managerRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    WidgetRepository widgetRepo;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    JwtTokenProvider tokenProvider;
    
    @Autowired
    ApplicationEventPublisher eventPublisher;
    
    @Autowired
    ContactcenterService contactcenterService;
    
    @Autowired
    PersonnelService personnelService;

    @Autowired
    UserService userService;
    
    @Autowired
    ManagerService managerService;
    
    @Autowired
    JokkoMailSender jokkoMailSender;
    
    @Autowired
	private AgentRepository personnelRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @Autowired
    EslServices eslService;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    	
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, WebRequest request) throws MessagingException, IOException {

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
        
        
        if(serviceRepository.existsByDomaine(signUpRequest.getDomaine())) {
            return new ResponseEntity(new ApiResponse(false, "Le nom de domaine existe déjà !"),
                    HttpStatus.BAD_REQUEST);
        }
        
        // Creating user's account
        Manager manager = new Manager(signUpRequest.getFirstname(), signUpRequest.getLastname(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        Manager result = managerRepository.save(manager);
        
        Service service = new Service();
        
		if(signUpRequest.getServiceType().equalsIgnoreCase("SERVICE_CHAT")) {
            return null;
		}else if(signUpRequest.getServiceType().equalsIgnoreCase("SERVICE_CALL")) {
			service.setTypeService(ServiceType.SERVICE_CALL);	
		}
        
		service.setManager(manager);
		service.setContactId("CONTACTCENTER_"+UUID.randomUUID().toString());
		service.setDomaine(signUpRequest.getDomaine());
		service.setEnabled(true);
        
        contactcenterService.createContactcenter(service);
        
        try {
	        String appUrl = request.getContextPath();
	        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(result, request.getLocale(), appUrl));
        } catch (Exception me) {
        	System.out.println("message erreur erreur "+me.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/me")
                .buildAndExpand(result.getEmail()).toUri();

		// Configuration personnel dans serveur freeswitch
		System.out.println("loading............. "+ service.getDomaine());
    	eslService.reloadService(service.getDomaine());
    	
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
    
    @GetMapping("/regitrationConfirm")
    public ResponseEntity<?> confirmRegistration(WebRequest request, @RequestParam("token") String token) {
      
        Locale locale = request.getLocale();
         
        VerificationToken verificationToken = managerService.getVerificationToken(token);
        
        if (verificationToken == null) {
            return new ResponseEntity(new ApiResponse(false, "Ce lien est invalide !"),
                    HttpStatus.BAD_REQUEST);
        }
         
        Manager manager = verificationToken.getManager();
        
        Calendar cal = Calendar.getInstance();
        
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return new ResponseEntity(new ApiResponse(false, "Ce lien est invalide !"),
                    HttpStatus.BAD_REQUEST);
        } 
        
        Role userRole = roleRepository.findByName(RoleName.ROLE_MANAGER)
        .orElseThrow(() -> new AppException("User Role not set."));
        
        Set<Role> userRoles = new HashSet<>();
        
        userRoles.add(userRole);

        manager.setRoles(userRoles);

        manager.setEnabled(true); 
        managerRepository.save(manager); 

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
        
        VerificationToken newToken = managerService.generateNewVerificationToken(vToken);
         
        User user = managerService.getManagerByToken(newToken.getToken());
               
		jokkoMailSender.sendMailResetPassword(user, newToken.getToken());
		
		return ResponseEntity.accepted().body(new ApiResponse(true, "Un e-mail de réinitialisation a été envoyé sur votre adresse e-mail : "+user.getEmail()));
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<?>  resetPassword(@Valid @RequestBody ResetPasswordRequest passwordRequest) throws MessagingException, IOException {
    	
    	Optional<User> isUser = userRepository.findByEmail(passwordRequest.getEmail());
		
		if (isUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Email not found"),
                    HttpStatus.BAD_REQUEST);
		}
		
		User user = isUser.get();
		
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
    
    @GetMapping("/checkEmailAvailability/{email}")
    public UserIdentityAvailability checkEmailAvailability(@PathVariable(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }
    
}