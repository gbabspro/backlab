package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewServiceRequest {
	
    @NotBlank
    @Size(min = 3, max = 15)
    private String serviceType;
    
    @NotBlank
    private String domaine;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getDomaine() {
		return domaine;
	}

	public void setDomaine(String domaine) {
		this.domaine = domaine;
	}

    
}
