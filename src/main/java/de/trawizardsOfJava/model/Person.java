package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.sql.rowset.serial.SerialArray;
import java.io.Serializable;

@Data
@Entity
public class Person implements Serializable {
	private String name;
	@Id
	private String benutzername;
	private String email;
	private boolean admin;
}
