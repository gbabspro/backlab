package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.NotBlank;

public class UpdatePasswordRequest {
	
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String password;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
