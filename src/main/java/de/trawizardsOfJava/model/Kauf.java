package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Kauf {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String kaeufer;
	@OneToOne
	private ArtikelKaufen artikel;
	private String verkaeufer;

	public Kauf() {
	}

	public Kauf(ArtikelKaufen artikel, String kaeufer) {
		this.artikel = artikel;
		this.kaeufer = kaeufer;
		this.verkaeufer = artikel.getVerkaeufer();
	}
}
