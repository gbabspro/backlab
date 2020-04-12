package com.jokkoapps.jokkoapps.payload;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class OperatorSummary {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String sipExtension;
    private String sipPassword;
    private Collection<? extends GrantedAuthority> authorities;

    public OperatorSummary(Long id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
       
    }

    public OperatorSummary(Long id, String firstname, String lastname, String email, String extension, String sipPassword, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.sipExtension = extension;
        this.sipPassword = sipPassword;
        this.authorities = authorities;
    }
    
    public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

	public String getSipExtension() {
		return sipExtension;
	}

	public void setSipExtension(String sipExtension) {
		this.sipExtension = sipExtension;
	}

	public String getSipPassword() {
		return sipPassword;
	}

	public void setSipPassword(String sipPassword) {
		this.sipPassword = sipPassword;
	}

}