package com.example.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Person {
	private String name;
	@Id
	private String benutzername;
	private String email;
	private boolean admin;
}
