package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateUserProfile {
    @NotBlank
    @Size(min = 4, max = 40)
    private String firstname;

    @NotBlank
    @Size(min = 3, max = 15)
    private String lastname;

    @Size(max = 40)
    private String phone;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
    
    
}
