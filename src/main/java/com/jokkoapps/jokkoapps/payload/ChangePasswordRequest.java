package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordRequest {
	
    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

	@NotBlank
	private String token;
    
    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
