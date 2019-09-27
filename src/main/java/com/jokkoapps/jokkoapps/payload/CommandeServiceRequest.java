package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommandeServiceRequest {
	
    @NotBlank
    @Size(min = 3, max = 15)
    private String serviceType;
    
    @NotBlank
    @Size(min = 3, max = 15)
    private String serviceName;
    
    @NotBlank
    @Size(max = 100)
    private String organisation;
    
    @NotBlank
    @Size(min = 3, max = 15)
    private String offreName;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getOffreName() {
		return offreName;
	}

	public void setOffreName(String offreName) {
		this.offreName = offreName;
	}
    
    

}
