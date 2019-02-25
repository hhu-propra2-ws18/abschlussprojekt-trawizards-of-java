package de.trawizardsOfJava.data;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Konflikt;
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
public class KonfliktRepositoryTest {
	@Autowired
	private KonfliktRepository konflikte;
	@Autowired
	private RueckgabeRepository rueckgaben;
	@Autowired
	private ArtikelRepository artikel;

	@Test
	public void speicherKonflikt() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		Ausleihe ausleihe = new Ausleihe(artikel, null, "bar");
		Rueckgabe rueckgabe = new Rueckgabe(ausleihe);
		Konflikt konflikt = new Konflikt();
		konflikt.setKonflikt(rueckgabe, "verursacher", "absender");
		this.artikel.save(artikel);
		this.rueckgaben.save(rueckgabe);
		this.konflikte.save(konflikt);

		ArrayList<Konflikt> konfliktListe = this.konflikte.findAllByInBearbeitung("offen");

		Assertions.assertThat(konfliktListe.get(0).getAbsenderMail()).isEqualTo("absender");
	}

	@Test
	public void mehrereKonflikte() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		Ausleihe ausleihe = new Ausleihe(artikel, null, "bar");
		Rueckgabe rueckgabe = new Rueckgabe(ausleihe);
		Konflikt konflikt = new Konflikt();
		konflikt.setKonflikt(rueckgabe, "verursacher", "absender");
		this.artikel.save(artikel);
		this.rueckgaben.save(rueckgabe);
		this.konflikte.save(konflikt);
		ausleihe = new Ausleihe(artikel, null, "rab");
		rueckgabe = new Rueckgabe(ausleihe);
		konflikt = new Konflikt();
		konflikt.setKonflikt(rueckgabe, "andererVerursacher", "absender");
		this.rueckgaben.save(rueckgabe);
		this.konflikte.save(konflikt);

		ArrayList<Konflikt> konfliktListe = this.konflikte.findAllByInBearbeitung("offen");
		System.out.println(konfliktListe);

		Assertions.assertThat(konfliktListe.get(0).getAbsenderMail()).isEqualTo("absender");
		Assertions.assertThat(konfliktListe.get(1).getVerursacherMail()).isEqualTo("andererVerursacher");
	}
}