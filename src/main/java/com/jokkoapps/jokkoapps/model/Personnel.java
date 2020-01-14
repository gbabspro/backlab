package com.jokkoapps.jokkoapps.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "personnel")
@PrimaryKeyJoinColumn(name = "id")
public class Personnel extends User{
    

	@JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @NotBlank
    @Size(max = 160)
    @Column(unique = true)
    private String uuidPers;
    
    @Column(name = "is_default_pers")
    private boolean isDefaultPers;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "extension_id", referencedColumnName = "id")
    private Extension extension;
    
	public Personnel() {
		super();
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getUuidPers() {
		return uuidPers;
	}

	public void setUuidPers(String uuidPers) {
		this.uuidPers = uuidPers;
	}

	public Extension getExtension() {
		return extension;
	}

	public void setExtension(Extension extension) {
		this.extension = extension;
	}
    
	public boolean isDefaultPers() {
		return isDefaultPers;
	}

	public void setDefaultPers(boolean isDefaultPers) {
		this.isDefaultPers = isDefaultPers;
	}

	public String getEmail() {
		
		if(this.isDefaultPers()) {
			return this.getService().getManager().getEmail();
		}
		
		return email;
	}
    
	
	public String getFirstname() {
		
		if(this.isDefaultPers()) {
			return this.getService().getManager().getFirstname();
		}
		
		return firstname;
	}

	public String getLastname() {
		
		if(this.isDefaultPers()) {
			return this.getService().getManager().getLastname();
		}
		
		return lastname;
	}

}
