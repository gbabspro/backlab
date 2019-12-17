package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.*;

public class SignUpRequest {
	
    @NotBlank
    @Size(min = 4, max = 40)
    private String firstname;

    @NotBlank
    @Size(min = 3, max = 15)
    private String lastname;

    @NotBlank
    @Size(max = 80)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
    
    @NotBlank
    @Size(min = 6, max = 160)
    private String domaine;
    
    @NotBlank
    @Size(min = 6, max = 20)
    private String serviceType;


    public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getDomaine() {
		return domaine;
	}

	public void setDomaine(String domaine) {
		this.domaine = domaine;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
    
    
}