package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.security.SecurityConfig;
import de.trawizardsOfJava.security.SecurityPersonenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AppController.class)
public class AppControllerTest {

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
	KonfliktRepository konfliktRepository;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@MockBean
	IMailService iMailService;

	@Test
	public void isOk() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void loginBadCredentials() throws Exception {
		when(securityPersonenService.loadUserByUsername("user1")).thenThrow(new UsernameNotFoundException("Invalid Username"));
		mvc.perform(post("/anmeldung")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("username", "user1")
			.param("password", "password1")).andExpect(redirectedUrl("/anmeldung?error"));
	}

	@Test
	public void loginWrongPassword() throws Exception {
		Person person1 = new Person();
		person1.setBenutzername("test");
		person1.setEmail("test@mail.com");
		person1.setName("test");
		person1.setPasswort(SecurityConfig.encoder().encode("1234"));
		person1.setRolle("ROLE_USER");
		benutzerRepository.save(person1);
		UserDetails userDetails = User.builder()
				.username(person1.getBenutzername())
				.password(person1.getPasswort())
				.authorities(person1.getRolle())
				.build();
		when(securityPersonenService.loadUserByUsername("test")).thenReturn(userDetails);
		mvc.perform(post("/anmeldung")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("password", "12345")).andExpect(redirectedUrl("/anmeldung?error"));
	}

	@Test
	public void loginCorrectCredentials() throws Exception {
		Person person1 = new Person();
		person1.setBenutzername("test");
		person1.setEmail("test@mail.com");
		person1.setName("test");
		person1.setPasswort(SecurityConfig.encoder().encode("1234"));
		person1.setRolle("ROLE_USER");
		benutzerRepository.save(person1);
		UserDetails userDetails = User.builder()
			.username(person1.getBenutzername())
			.password(person1.getPasswort())
			.authorities(person1.getRolle())
			.build();
		when(securityPersonenService.loadUserByUsername("test")).thenReturn(userDetails);
		mvc.perform(post("/anmeldung")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("username", "test")
			.param("password", "1234")).andExpect(redirectedUrl("/"));
	}

	@Test
	public void registrierung() throws Exception{
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");

		mvc.perform(post("/registrierung")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("benutzername", test.getBenutzername())
				.param("name", test.getName())
				.param("email", test.getEmail())
				.param("passwort", test.getPasswort())).andExpect(view().name("backToTheFuture"));

		verify(benutzerRepository).save(any(Person.class));
	}

	@Test
	public void registrierungFehler() throws Exception {
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));

		mvc.perform(post("/registrierung")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("benutzername", test.getBenutzername())
				.param("name", test.getName())
				.param("email", test.getEmail())
				.param("passwort", test.getPasswort())).andExpect(view().name("registrierung"));
	}

	@Test
	@WithMockUser(username = "foo", authorities = "ROLE_USER")
	public void bearbeiteMeinenAccount() throws Exception{
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");

		when(benutzerRepository.findByBenutzername(test.getBenutzername())).thenReturn(Optional.of(test));

		mvc.perform(get("/account/foo/bearbeitung")).andExpect(view().name("benutzerverwaltung"));
	}

	@Test
	@WithMockUser(username = "foo", authorities = "ROLE_USER")
	public void bearbeiteAnderenAccount() throws Exception{
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
	@WithMockUser(username = "foo", authorities = "ROLE_USER")
	public void chargePropayAccount() throws Exception{
		String amount = "100";
		mvc.perform(post("/account/foo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("amount", amount)).andExpect(view().name("backToTheFuture"));
	}
}
