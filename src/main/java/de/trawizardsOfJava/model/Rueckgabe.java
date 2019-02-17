package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Rueckgabe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String ausleihender;
	@OneToOne
	private Artikel artikel;
	private Verfuegbarkeit verfuegbarkeit;
	private String verleiherName;
	private int proPayId;

	public Rueckgabe(Ausleihe ausleihe) {
		this.id = ausleihe.getId();
		this.ausleihender = ausleihe.getAusleihender();
		this.artikel = ausleihe.getArtikel();
		this.verfuegbarkeit = ausleihe.getVerfuegbarkeit();
		this.verleiherName = ausleihe.getVerleiherName();
		this.proPayId = ausleihe.getProPayId();
	}
	
	public Rueckgabe(){}
}