package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AusleiheRepositoryTest {
	@Autowired
	private AusleiheRepository ausleihen;
	@Autowired
	private ArtikelRepository artikel;

	@Test
	public void speicherAusleihe() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		Ausleihe ausleihe = new Ausleihe(artikel, null, "bar");
		this.artikel.save(artikel);
		this.ausleihen.save(ausleihe);

		ArrayList<Ausleihe> ausleihenListe = this.ausleihen.findByVerleiherName(artikel.getVerleiherBenutzername());

		Assertions.assertThat(ausleihenListe.get(0).getAusleihender()).isEqualTo("bar");
	}

	@Test
	public void mehrereAusleihen() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		Ausleihe ausleihe = new Ausleihe(artikel, null, "bar");
		this.artikel.save(artikel);
		this.ausleihen.save(ausleihe);
		ausleihe = new Ausleihe(artikel, null, "rab");
		this.ausleihen.save(ausleihe);

		ArrayList<Ausleihe> ausleihenListe = this.ausleihen.findByVerleiherName(artikel.getVerleiherBenutzername());

		Assertions.assertThat(ausleihenListe.get(0).getAusleihender()).isEqualTo("bar");
		Assertions.assertThat(ausleihenListe.get(1).getAusleihender()).isEqualTo("rab");
	}
}