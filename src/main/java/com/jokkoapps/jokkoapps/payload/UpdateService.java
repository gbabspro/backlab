package com.jokkoapps.jokkoapps.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateService {
    @NotBlank
    @Size(min = 3, max = 15)
    private String serviceName;
    
    @NotBlank
    @Size(max = 100)
    private String organisation;

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
    
    
}
