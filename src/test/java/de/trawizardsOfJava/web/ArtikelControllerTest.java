package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelKaufenRepository;
import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.ArtikelKaufen;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import de.trawizardsOfJava.security.SecurityPersonenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(ArtikelController.class)
public class ArtikelControllerTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	ArtikelRepository artikelRepository;

	@MockBean
	ArtikelKaufenRepository artikelKaufenRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@Test
	@WithMockUser(username = "foo")
	public void erstelleArtikel_leihen() throws Exception{
		Artikel test = new Artikel();
		test.setVerleiherBenutzername("foo");
		test.setArtikelName("Schaufel");
		test.setKaution(10);
		test.setPreis(10);
		test.setVerfuegbarkeit(new Verfuegbarkeit("22/02/2019 - 22/02/2019"));
		test.setBeschreibung("Schaufel");
		test.setStandort("foo");
		test.setFotos(new ArrayList<>());

		mvc.perform(post("/account/foo/erstelleArtikel/leihen")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("artikelName", test.getArtikelName())
				.param("beschreibung", test.getBeschreibung())
				.param("standort", test.getStandort())
				.param("preis", "" + test.getPreis())
				.param("kaution", "" + test.getKaution())
				.param("verleiherBenutzername", test.getVerleiherBenutzername())
				.param("daterange", "22/03/2019 - 22/03/2019"));

		verify(artikelRepository).save(any(Artikel.class));
	}

	@Test
	@WithMockUser(username = "foo")
	public void aendereArtikel() throws Exception{
		Artikel test = new Artikel();
		test.setVerleiherBenutzername("foo");
		test.setArtikelName("Schaufel");
		test.setKaution(10);
		test.setPreis(10);
		test.setVerfuegbarkeit(new Verfuegbarkeit("22/02/2019 - 22/02/2019"));
		test.setBeschreibung("Schaufel");
		test.setStandort("foo");
		test.setId(1L);

		when(artikelRepository.findById(1L)).thenReturn(Optional.of(test));

		mvc.perform(post("/account/foo/aendereArtikel/" + 1L)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("artikelName", test.getArtikelName())
				.param("beschreibung", test.getBeschreibung())
				.param("standort", test.getStandort())
				.param("preis", "" + test.getPreis())
				.param("kaution", "" + test.getKaution())
				.param("daterange", "22/02/2019 - 22/02/2019")
				.param("verleiherBenutzername", test.getVerleiherBenutzername())
				.param("id", ""+ 1L));

		verify(artikelRepository).save(test);
	}

	@Test
	public void detailansicht() throws Exception{
		Artikel test = new Artikel();
		test.setVerleiherBenutzername("foo");
		test.setArtikelName("Schaufel");
		test.setKaution(10);
		test.setPreis(10);
		test.setVerfuegbarkeit(new Verfuegbarkeit("22/02/2019 - 22/02/2019"));
		test.setBeschreibung("Schaufel");
		test.setStandort("foo");
		test.setId(1L);

		when(artikelRepository.findById(1L)).thenReturn(Optional.of(test));

		mvc.perform(get("/detail/" + 1L)).andExpect(status().isOk());
	}

	@Test
	public void select() {

	}

	@Test
	@WithMockUser(username = "foo")
	public void erstelleArtikel_kaufen() throws Exception {
		ArtikelKaufen test = new ArtikelKaufen();
		test.setVerkaeufer("foo");
		test.setArtikelName("Schaufel");
		test.setPreis(10);
		test.setBeschreibung("Schaufel");
		test.setStandort("foo");

		mvc.perform(post("/account/foo/erstelleArtikel/kaufen")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("artikelName", test.getArtikelName())
				.param("beschreibung", test.getBeschreibung())
				.param("standort", test.getStandort())
				.param("preis", "" + test.getPreis())
				.param("verkaeufer", test.getVerkaeufer()));

		verify(artikelKaufenRepository).save(test);
	}

	@Test
	public void artikelDetailFoto() {
	}
}
