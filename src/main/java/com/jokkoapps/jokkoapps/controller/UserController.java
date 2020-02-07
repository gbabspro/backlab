package com.jokkoapps.jokkoapps.controller;


import com.jokkoapps.jokkoapps.exception.ResourceNotFoundException;
import com.jokkoapps.jokkoapps.model.User;
import com.jokkoapps.jokkoapps.payload.*;
import com.jokkoapps.jokkoapps.repository.UserRepository;

import com.jokkoapps.jokkoapps.security.UserPrincipal;
import com.jokkoapps.jokkoapps.services.UserService;
import com.jokkoapps.jokkoapps.security.CurrentUser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('MANAGER') or hasRole('AGENT')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {

        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getFirstname(), currentUser.getLastname(), currentUser.getEmail(), currentUser.getPhone(), currentUser.getAuthorities());

        return userSummary;
    }
    
    @PostMapping("/user/updatePassword")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changeUserPassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest, @CurrentUser UserPrincipal currentUser) {
    	Optional<User> opntionalUser = userRepository.findByEmail(currentUser.getEmail());
    	
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		String oldPassword = updatePasswordRequest.getOldPassword();
		
		String password = updatePasswordRequest.getPassword();
		
		User user = opntionalUser.get();
         
        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            return new ResponseEntity(new ApiResponse(false, "Votre ancien mot de passe n'est pas valide !"),
                    HttpStatus.BAD_REQUEST);
        }
        
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        return ResponseEntity.accepted().body(new ApiResponse(true, "Votre mot de passe a bien étè modifié !"));
    }
    
    
    @GetMapping("/user/php/gen")
    public ResponseEntity<?> phpGen() throws Exception {
    	
    	URL url = new URL("http://srv.babacargaye.com/testfile/filegenerator.php?name="+RandomStringUtils.randomAlphabetic(15)+".js"+
    			"&sipuserpass=passer"+
    			"&sipuser=babs"+
    			"&center=centre&theme=cfcfcf");
    	
    	HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
    	cnx.connect();
    	
    	if( cnx.getResponseCode() == HttpURLConnection.HTTP_OK ){
    		
    		BufferedReader input = new BufferedReader(new InputStreamReader(
    				cnx.getInputStream()));
    		
    		String inputLine;
            while ((inputLine = input.readLine()) != null) 
                System.out.println(inputLine);
            input.close();
            
    	}else{
    	    
    	    System.out.println("cnx ko "+cnx.getErrorStream());
    	}

        
        return ResponseEntity.accepted().body("edede");
    }
    
    @PostMapping("/user/updateEmail")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changeUserEmail(@Valid @RequestBody UpdateEmailRequest updateEmailRequest, @CurrentUser UserPrincipal currentUser) {
    	Optional<User> opntionalUser = userRepository.findByEmail(currentUser.getEmail());
    	
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		String oldPassword = updateEmailRequest.getPassword();
		
		String newEmail = updateEmailRequest.getEmail();
		
		User user = opntionalUser.get();
         
        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            return new ResponseEntity(new ApiResponse(false, "Votre ancien mot de passe n'est pas valide !"),
                    HttpStatus.BAD_REQUEST);
        }
        
        user.setEmail(newEmail);
        userRepository.save(user);
        
        return ResponseEntity.accepted().body(new ApiResponse(true, "Votre adresse e-mail a bien étè modifié !"));
    }
    
    
    @PostMapping("/user/updateProfil")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changeUserProfile(@Valid @RequestBody UpdateUserProfile updateUserRequest, @CurrentUser UserPrincipal currentUser) {
    	Optional<User> opntionalUser = userRepository.findByEmail(currentUser.getEmail());
    	
		if (opntionalUser.isPresent() != true) {
            return new ResponseEntity(new ApiResponse(false, "Vous n'étes pas autorisé à effectuer cette opération !"),
                    HttpStatus.BAD_REQUEST);
		}
		
		User user = opntionalUser.get();
		
		user.setFirstname(updateUserRequest.getFirstname());
		user.setLastname(updateUserRequest.getLastname());

        userRepository.save(user);
        
        return ResponseEntity.accepted().body(new ApiResponse(true, "Votre profile a bien étè modifié !"));
    }

}