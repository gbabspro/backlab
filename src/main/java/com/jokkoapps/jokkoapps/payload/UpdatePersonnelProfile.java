package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdatePersonnelProfile {
	
    @NotBlank
    @Size(min = 4, max = 40)
    private String firstname;

    @NotBlank
    @Size(min = 4, max = 15)
    private String lastname;

    @Size(max = 80)
    @Email
    private String email;
    
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

}