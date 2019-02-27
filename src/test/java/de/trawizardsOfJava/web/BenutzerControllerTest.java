package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import de.trawizardsOfJava.proPay.ProPay;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(BenutzerController.class)
public class BenutzerControllerTest {
	@Autowired
	MockMvc mvc;

	@MockBean
	BenutzerRepository benutzerRepository;

	@MockBean
	ArtikelRepository artikelRepository;

	@MockBean
	AusleiheRepository ausleiheRepository;

	@MockBean
	RueckgabeRepository rueckgabeRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	IProPaySchnittstelle proPaySchnittstelle;

	@MockBean
	KaufRepository kaufRepository;

	@MockBean
	ArtikelKaufenRepository artikelKaufenRepository;

	@MockBean
	IMailService iMailService;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@MockBean
	BewertungRepository bewertungRepository;

	@Test
	@WithMockUser(username = "foo", authorities = "ROLE_USER")
	public void benutzerprofil() throws Exception {
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));
		when(artikelRepository.findByVerleiherBenutzername(test.getBenutzername())).thenReturn(new ArrayList<>());
		when(ausleiheRepository.findByAusleihender(test.getBenutzername())).thenReturn(new ArrayList<>());
		when(proPaySchnittstelle.getEntity("foo")).thenReturn(new ProPay());

		mvc.perform(get("/account/foo")).andExpect(status().isOk());
	}

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

	@Test
	@WithMockUser(username="foo")
	public void bearbeiten() throws Exception{
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));

		mvc.perform(post("/account/foo/bearbeitung").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "bar")
				.param("email", test.getEmail())).andExpect(view().name("backToTheFuture"));
		verify(benutzerRepository).save(any(Person.class));
	}

	@Test
	@WithMockUser(username="foo", authorities = "ROLE_USER")
	public void loescheNachricht() throws Exception{
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");
		Message message = new Message();
		message.setId(1L);
		ArrayList<Message> nachrichten = new ArrayList<>();
		nachrichten.add(message);

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));
		when(messageRepository.findByEmpfaenger(test.getBenutzername())).thenReturn(nachrichten);
		when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

		mvc.perform(post("/account/foo/nachrichten").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "" + message.getId()));
		verify(messageRepository).delete(any(Message.class));
	}
}