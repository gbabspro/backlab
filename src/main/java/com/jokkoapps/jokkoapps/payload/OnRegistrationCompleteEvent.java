package com.jokkoapps.jokkoapps.payload;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.jokkoapps.jokkoapps.model.Manager;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private Manager manager;
 
    public OnRegistrationCompleteEvent(
      Manager manager, Locale locale, String appUrl) {
        super(manager);
         
        this.manager = manager;
        this.locale = locale;
        this.appUrl = appUrl;
    }

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	
     
    
}