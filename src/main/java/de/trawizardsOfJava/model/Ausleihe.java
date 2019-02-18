package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Ausleihe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String ausleihender;
	@OneToOne
	private Artikel artikel;
	private Verfuegbarkeit verfuegbarkeit;
	private String verleiherName;
	private boolean accepted = false;
	private int proPayId;

	public int berechneGesamtPreis() {
		return this.artikel.getKaution() + (this.verfuegbarkeit.berechneZwischenTage() * this.artikel.getPreis());
	}
}