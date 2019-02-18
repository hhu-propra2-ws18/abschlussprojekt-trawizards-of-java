package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Person {
	@Id
	private String benutzername;
	private String name;
	private String email;
	private String rolle;
	private String passwort;
}