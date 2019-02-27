package de.trawizardsOfJava.model;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Bewertung {

	@Id
	@GeneratedValue
	private Long id;
	private String ueberschrift;
	private String text;
	private String bewertungFuer;
	private String bewertungVon;
}
