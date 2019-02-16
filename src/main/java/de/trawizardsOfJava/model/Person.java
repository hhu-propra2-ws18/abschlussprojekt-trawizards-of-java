package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class Person implements Serializable {
	@Id
	private String benutzername;
	private String name;
	private String email;
	private String rolle;
	private String passwort;
}