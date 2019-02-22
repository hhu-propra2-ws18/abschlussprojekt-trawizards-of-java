package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Artikel;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ArtikelRepositoryTest {
	@Autowired
	private ArtikelRepository artikel;

	@Test
	public void speicherArtikel(){
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		this.artikel.save(artikel);

		Artikel datenbankArtikel = this.artikel.findById(artikel.getId()).get();

		Assertions.assertThat(datenbankArtikel.getArtikelName()).isEqualTo("Bagger");
	}

	@Test
	public void mehrereArtikel() {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Bagger");
		artikel.setBeschreibung("Das ist ein Bagger");
		artikel.setStandort("Duesseldorf");
		artikel.setPreis(0);
		artikel.setKaution(0);
		this.artikel.save(artikel);

		Artikel artikel2 = new Artikel();
		artikel2.setVerleiherBenutzername("foo");
		artikel2.setArtikelName("Bagger");
		artikel2.setBeschreibung("Das ist auch ein Bagger");
		artikel2.setStandort("Duesseldorf");
		artikel2.setPreis(0);
		artikel2.setKaution(0);
		this.artikel.save(artikel2);

		ArrayList<Artikel> datenbankArtikel = this.artikel.findByVerleiherBenutzername("foo");

		Assertions.assertThat(datenbankArtikel.get(0).getArtikelName()).isEqualTo("Bagger");
		Assertions.assertThat(datenbankArtikel.get(1).getBeschreibung()).isEqualTo("Das ist auch ein Bagger");
	}
}