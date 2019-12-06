package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommandeServiceRequest {
	
    @NotBlank
    @Size(min = 3, max = 15)
    private String serviceType;
    
    @NotBlank
    private String domaine_name;
    
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getDomaine_name() {
		return domaine_name;
	}

	public void setDomaine_name(String domaine_name) {
		this.domaine_name = domaine_name;
	}
    
}
