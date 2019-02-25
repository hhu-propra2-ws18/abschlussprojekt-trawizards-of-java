package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.*;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(KonfliktController.class)
public class KonfliktControllerTest {
	@Autowired
	MockMvc mvc;

	@MockBean
	BenutzerRepository benutzerRepository;

	@MockBean
	RueckgabeRepository rueckgabeRepository;

	@MockBean
	KonfliktRepository konfliktRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	IProPaySchnittstelle proPaySchnittstelle;

	@MockBean
	IMailService iMailService;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@Test
	@WithMockUser(username="foo")
	public void sendeKonflikt() throws Exception{
		Artikel artikel = new Artikel();
		artikel.setVerleiherBenutzername("foo");
		artikel.setArtikelName("Schaufel");
		artikel.setKaution(10);
		artikel.setPreis(10);
		artikel.setVerfuegbarkeit(new Verfuegbarkeit("20/02/2019 - 24/02/2019"));
		artikel.setBeschreibung("Schaufel");
		artikel.setStandort("foo");
		artikel.setId(1L);

		Rueckgabe rueckgabe = new Rueckgabe();
		rueckgabe.setAngenommen(false);
		rueckgabe.setArtikel(artikel);
		rueckgabe.setAusleihender("bar");
		rueckgabe.setVerleiherName("foo");
		rueckgabe.setId(1L);

		Person person = new Person();
		person.setEmail("email");

		when(rueckgabeRepository.findById(rueckgabe.getId())).thenReturn(Optional.of(rueckgabe));
		when(benutzerRepository.findByBenutzername(anyString())).thenReturn(Optional.of(person));

		mvc.perform(post("/account/foo/konflikt/send/"+1L).accept(MediaType.APPLICATION_FORM_URLENCODED)
				.param("beschreibung", "beschreibung")
				.param("id", "1"));

		verify(konfliktRepository).save(any(Konflikt.class));
	}

	//TODO loeseKonflikt()... ProPay ist halt Scheisse implementiert....
}