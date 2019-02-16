package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Konflikt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String beschreibung;
	@OneToOne
	private Rueckgabe rueckgabe;
}