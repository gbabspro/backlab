package com.jokkoapps.jokkoapps.payload;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class UserSummary {
	
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Collection<? extends GrantedAuthority> authorities;

    public UserSummary(Long id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
       
    }

    public UserSummary(Long id, String firstname, String lastname, String email, String phone, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.authorities = authorities;
    }
    
    public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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


}