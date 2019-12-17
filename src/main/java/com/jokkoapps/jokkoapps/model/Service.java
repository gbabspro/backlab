package com.jokkoapps.jokkoapps.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    @Size(max = 100)
    @Column(unique = true)
    private String contactId;
    
    @NotBlank
    @Size(max = 163)
    @Column(unique = true)
    private String domaine;
    
    @Column(unique = false)
    private ServiceType typeService;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    
    @NotBlank
    @Size(max = 160)
    @Column(unique = true)
    private String defaultSipUser;
    
    @NotBlank
    @Size(max = 160)
    @Column(unique = true)
    private String defaultSipPassword;

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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
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

	

	public String getDomaine() {
		return domaine;
	}

	public void setDomaine(String domaine) {
		this.domaine = domaine;
	}

	public String getDefaultSipUser() {
		return defaultSipUser;
	}

	public void setDefaultSipUser(String defaultSipUser) {
		this.defaultSipUser = defaultSipUser;
	}

	public String getDefaultSipPassword() {
		return defaultSipPassword;
	}

	public void setDefaultSipPassword(String defaultSipPassword) {
		this.defaultSipPassword = defaultSipPassword;
	}	
	
	
}
