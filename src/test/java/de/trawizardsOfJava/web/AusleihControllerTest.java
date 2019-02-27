package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.KaufRepository;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import de.trawizardsOfJava.data.RueckgabeRepository;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Rueckgabe;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import de.trawizardsOfJava.proPay.ProPay;
import de.trawizardsOfJava.proPay.Reservierung;
import de.trawizardsOfJava.security.SecurityPersonenService;
import org.junit.Assert;
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
	KaufRepository kaufRepository;

	@MockBean
	RueckgabeRepository rueckgabeRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	IProPaySchnittstelle proPaySchnittstelle;

	@MockBean
	IMailService iMailService;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@Test
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

		ProPay proPay = new ProPay();
		proPay.setAmount(100L);
		proPay.setReservations(new ArrayList<>());

		when(artikelRepository.findById(1L)).thenReturn(Optional.of(artikel));
		when(proPaySchnittstelle.ping()).thenReturn(true);
		when(proPaySchnittstelle.getEntity("bar")).thenReturn(proPay);

		mvc.perform(post("/account/bar/artikel/" + 1L + "/anfrage")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.param("daterange", "22/02/2019 - 22/02/2019"))
				.andExpect(view().name("backToTheFuture"));
		verify(ausleiheRepository).save(any(Ausleihe.class));
	}

	@Test
	@WithMockUser(username = "bar")
	public void anfrageAbgelehnt() throws Exception {
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

		when(ausleiheRepository.findById(1L)).thenReturn(Optional.of(ausleihe));

		mvc.perform(post("/account/bar/ausleihenuebersicht")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "" + 1L)
				.param("art", "abgelehnt"))
				.andExpect(view().name("ausleihenUebersicht"));
		verify(ausleiheRepository).delete(ausleihe);
	}

	@Test
	@WithMockUser(username = "bar")
	public void anfrageAngenommen() throws Exception {
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

		ProPay proPay = new ProPay();
		proPay.setAmount(100L);
		ArrayList<Reservierung> reservierungen = new ArrayList<>();
		Reservierung reservierung = new Reservierung();
		reservierung.setAmount((long)artikel.getKaution());
		reservierung.setId(1L);
		reservierungen.add(reservierung);
		proPay.setReservations(reservierungen);

		when(ausleiheRepository.findById(1L)).thenReturn(Optional.of(ausleihe));
		when(proPaySchnittstelle.getEntity("bar")).thenReturn(proPay);
		when(proPaySchnittstelle.ping()).thenReturn(true);

		mvc.perform(post("/account/bar/ausleihenuebersicht")
			.accept(MediaType.APPLICATION_FORM_URLENCODED)
			.param("id", "" + 1L)
			.param("art", "angenommen"))
			.andExpect(view().name("ausleihenUebersicht"));
		verify(ausleiheRepository).save(any(Ausleihe.class));
	}

	@Test
	@WithMockUser(username = "bar")
	public void stelleRueckgabe() throws Exception {
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

		when(ausleiheRepository.findById(1L)).thenReturn(Optional.of(ausleihe));

		mvc.perform(post("/account/bar/ausgelieheneuebersicht")
			.accept(MediaType.APPLICATION_FORM_URLENCODED)
			.param("id", "" + 1L))
			.andExpect(view().name("ausgelieheneUebersicht"));
		verify(rueckgabeRepository).save(any(Rueckgabe.class));
	}


	@Test
	@WithMockUser(username = "foo")
	public void rueckgabeAkzeptiert() throws Exception {
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

		Rueckgabe rueckgabe = new Rueckgabe(ausleihe);
		rueckgabe.setId(1L);

		ProPay proPay = new ProPay();
		proPay.setAmount(1L);
		when(rueckgabeRepository.findById(rueckgabe.getId())).thenReturn(Optional.of(rueckgabe));
		when(proPaySchnittstelle.getEntity("foo")).thenReturn(proPay);
		when(proPaySchnittstelle.ping()).thenReturn(true);

		mvc.perform(post("/account/foo/zurueckgegebeneartikel").accept(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "" + rueckgabe.getId()))
				.andExpect(view().name("zurueckgegebeneartikel"));
		Assert.assertTrue(rueckgabe.isAngenommen());
		verify(rueckgabeRepository).save(rueckgabe);
	}
}