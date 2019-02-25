package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Rueckgabe;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RueckgabeRepositoryTest {
	@Autowired
	private RueckgabeRepository rueckgaben;
	@Autowired
	private ArtikelRepository artikel;

	@Test
	public void speicherRueckgabe() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		Ausleihe ausleihe = new Ausleihe(artikel, null, "bar");
		Rueckgabe rueckgabe = new Rueckgabe(ausleihe);
		this.artikel.save(artikel);
		this.rueckgaben.save(rueckgabe);

		ArrayList<Rueckgabe> rueckgabenListe = this.rueckgaben.findByVerleiherName(artikel.getVerleiherBenutzername());

		Assertions.assertThat(rueckgabenListe.get(0).getAusleihender()).isEqualTo("bar");
	}

	@Test
	public void mehrereRueckgaben() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		Ausleihe ausleihe = new Ausleihe(artikel, null, "bar");
		Rueckgabe rueckgabe = new Rueckgabe(ausleihe);
		this.artikel.save(artikel);
		this.rueckgaben.save(rueckgabe);
		ausleihe = new Ausleihe(artikel, null, "rab");
		rueckgabe = new Rueckgabe(ausleihe);
		this.rueckgaben.save(rueckgabe);

		ArrayList<Rueckgabe> rueckgabenListe = this.rueckgaben.findByVerleiherName(artikel.getVerleiherBenutzername());

		Assertions.assertThat(rueckgabenListe.get(0).getAusleihender()).isEqualTo("bar");
		Assertions.assertThat(rueckgabenListe.get(1).getAusleihender()).isEqualTo("rab");
	}
}