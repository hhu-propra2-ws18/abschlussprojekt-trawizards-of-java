package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
public class Person {
	@Id
	private String benutzername;
	private String name;
	private String email;
	private String rolle = "ROLE_USER";
	private String passwort;

}