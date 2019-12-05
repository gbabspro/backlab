package com.jokkoapps.jokkoapps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jokkoapps.jokkoapps.model.audit.DateAudit;

@Entity
@Table(name = "services")
public class Service extends DateAudit {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 40)
    private String serviceName;
    
    @NotBlank
    @Size(max = 40)
    @Column(unique = true)
    private String contactId;
    
    @Column(unique = false)
    private ServiceType typeService;
    
    @Size(max = 40)
    private String organisation;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    
    @ManyToOne(fetch=FetchType.EAGER, optional = false)
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    @Column(name = "enabled")
    private boolean enabled;

	public ServiceType getTypeService() {
		return typeService;
	}

	public void setTypeService(ServiceType typeService) {
		this.typeService = typeService;
	}

	public Service() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public Offre getOffre() {
		return offre;
	}

	public void setOffre(Offre offre) {
		this.offre = offre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
    
}
