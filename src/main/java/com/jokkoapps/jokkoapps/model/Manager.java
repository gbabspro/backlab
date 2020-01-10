package com.jokkoapps.jokkoapps.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
public class Manager extends User{

    @Size(max = 80)
    private String phone;
    
	public Manager() {

	}

    public Manager(String firstname, String lastname, String email, String password) {
    	super(firstname, lastname, email, password);
    }

}
