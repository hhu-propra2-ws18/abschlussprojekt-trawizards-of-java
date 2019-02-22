package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.RueckgabeRepository;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import de.trawizardsOfJava.proPay.ProPay;
import de.trawizardsOfJava.proPay.ProPaySchnittstelle;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringRunner.class)
@WebMvcTest(AusleihController.class)
public class AusleihControllerTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	AusleiheRepository ausleiheRepository;

	@MockBean
	ArtikelRepository artikelRepository;

	@MockBean
	RueckgabeRepository rueckgabeRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@MockBean
	ProPay proPay;

	@MockBean
	IMailService iMailService;

//TODO fix Test... ProPay laesst sich so nicht mocken...
	/*@Test
	@WithMockUser(username = "bar")
	public void stelleAnfrage() throws Exception {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Schaufel");
		artikel.setKaution(10);
		artikel.setPreis(10);
		artikel.setVerfuegbarkeit(new Verfuegbarkeit("20/02/2019 - 24/02/2019"));
		artikel.setBeschreibung("Schaufel");
		artikel.setStandort("foo");
		artikel.setId(1L);

		Ausleihe ausleihe = new Ausleihe();
		ausleihe.setArtikel(artikel);
		ausleihe.setAusleihender("bar");
		ausleihe.setVerfuegbarkeit(new Verfuegbarkeit("22/02/2019 - 22/02/2019"));
		ausleihe.setVerleiherName(artikel.getVerleiherBenutzername());
		ausleihe.setId(1L);

		when(ausleiheRepository.findByArtikel(artikel)).thenReturn(new ArrayList<>());
		when(artikelRepository.findById(1L)).thenReturn(Optional.of(artikel));
		when(ausleiheRepository.findByAusleihenderAndAccepted("bar", false)).thenReturn(new ArrayList<>());
		when(ProPaySchnittstelle.getEntity("bar")).thenReturn(new ProPay());
		when(proPay.genuegendGeld(ausleihe.berechneGesamtPreis(), new ArrayList<>())).thenReturn(true);

		mvc.perform(post("/account/bar/artikel/" + 1L + "/anfrage")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.param("daterange", "22/02/2019 - 22/02/2019")).andExpect(view().name("backToTheFuture"));
	}*/

	@Test
	@WithMockUser(username = "bar")
	public void verwalteAnfrage_abgelehnt() throws Exception {
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Schaufel");
		artikel.setKaution(10);
		artikel.setPreis(10);
		artikel.setVerfuegbarkeit(new Verfuegbarkeit("20/02/2019 - 24/02/2019"));
		artikel.setBeschreibung("Schaufel");
		artikel.setStandort("foo");
		artikel.setId(1L);

		Ausleihe ausleihe = new Ausleihe();
		ausleihe.setArtikel(artikel);
		ausleihe.setAusleihender("bar");
		ausleihe.setVerfuegbarkeit(new Verfuegbarkeit("22/02/2019 - 22/02/2019"));
		ausleihe.setVerleiherName(artikel.getVerleiherBenutzername());
		ausleihe.setId(1L);

		ArrayList<Ausleihe> ausleihen = new ArrayList<>();
		ausleihen.add(ausleihe);

		when(ausleiheRepository.findById(1L)).thenReturn(Optional.of(ausleihe));
		when(ausleiheRepository.findByVerleiherName("bar")).thenReturn(ausleihen);

		mvc.perform(post("/account/bar/ausleihenuebersicht")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "" + 1L)
				.param("art", "abgelehnt"));

		verify(ausleiheRepository).delete(ausleihe);
	}

	//TODO verwalteAnfrage_angenommen().... viel SPass mit ProPay.

	//TODO zurueckgegebeneArtikel()... ebenfalls ProPay.

}