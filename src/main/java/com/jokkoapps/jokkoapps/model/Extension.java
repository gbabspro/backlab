package com.jokkoapps.jokkoapps.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "extensions")
public class Extension {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotBlank
    @Size(max = 100)
    private String extension;
    
    @NotBlank
    @Size(max = 200)
    private String sipPassword;
    
    @Size(max = 160)
    private String displayName;
    
    @Size(max = 160)
    private String accountCode;
    
    @NotBlank
    @Size(max = 160)
    private String extensionType;
    
    public Extension() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getSipPassword() {
		return sipPassword;
	}

	public void setSipPassword(String sipPassword) {
		this.sipPassword = sipPassword;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getExtensionType() {
		return extensionType;
	}

	public void setExtensionType(String extensionType) {
		this.extensionType = extensionType;
	}

	
}
