package com.jokkoapps.jokkoapps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "agent")
@PrimaryKeyJoinColumn(name = "id")
public class Personnel extends User{
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Service service;

	public Personnel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
    
    
}
