package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.security.SecurityPersonenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(BenutzerController.class)
public class BenutzerControllerTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	AusleiheRepository ausleiheRepository;

	@MockBean
	BenutzerRepository benutzerRepository;

	@MockBean
	ArtikelRepository artikelRepository;

	@MockBean
	RueckgabeRepository rueckgabeRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@MockBean
	IMailService iMailService;

	@Test
	@WithMockUser(username = "foo", authorities = "ROLE_USER")
	public void bearbeiteMeinenAccount() throws Exception {
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));

		mvc.perform(get("/account/foo/bearbeitung")).andExpect(view().name("profilAendern"));
	}

	@Test
	@WithMockUser(username = "foo", authorities = "ROLE_USER")
	public void bearbeiteAnderenAccount() throws Exception {
		Person test = new Person();
		test.setBenutzername("bar");
		test.setName("bar");
		test.setEmail("bar");
		test.setPasswort("bar");
		test.setRolle("ROLE_USER");

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));

		mvc.perform(get("/account/bar/bearbeitung")).andExpect(status().is(403));
	}

}