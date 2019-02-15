package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Person;
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
	SecurityPersonenService securityPersonenService;

	@MockBean
	RueckgabeRepository rueckgabeRepository;

	@MockBean
	MessageRepository messageRepository;

	@MockBean
	KonfliktRepository konfliktRepository;

	@MockBean
	IMailService iMailService;

	@Test
	public void isOk() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void loginBadCredentials() throws Exception {
		when(securityPersonenService.loadUserByUsername("user1")).thenThrow(new UsernameNotFoundException("Invalid Username"));
		mvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("username", "user1")
			.param("password", "password1")).andExpect(redirectedUrl("/login?error"));
	}

	@Test
	public void loginWrongPassword() throws Exception {
		Person person1 = new Person();
		person1.setBenutzername("test");
		person1.setEmail("test@mail.com");
		person1.setName("test");
		SecurityConfig securityConfig = new SecurityConfig();
		person1.setPasswort(securityConfig.encoder().encode("1234"));
		person1.setRolle("ROLE_USER");
		benutzerRepository.save(person1);
		UserDetails userDetails = User.builder()
				.username(person1.getBenutzername())
				.password(person1.getPasswort())
				.authorities(person1.getRolle())
				.build();
		when(securityPersonenService.loadUserByUsername("test")).thenReturn(userDetails);
		mvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("password", "12345")).andExpect(redirectedUrl("/login?error"));
	}

	@Test
	public void loginCorrectCredentials() throws Exception {
		Person person1 = new Person();
		person1.setBenutzername("test");
		person1.setEmail("test@mail.com");
		person1.setName("test");
		SecurityConfig securityConfig = new SecurityConfig();
		person1.setPasswort(securityConfig.encoder().encode("1234"));
		person1.setRolle("ROLE_USER");
		benutzerRepository.save(person1);
		UserDetails userDetails = User.builder()
			.username(person1.getBenutzername())
			.password(person1.getPasswort())
			.authorities(person1.getRolle())
			.build();
		when(securityPersonenService.loadUserByUsername("test")).thenReturn(userDetails);
		mvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("username", "test")
			.param("password", "1234")).andExpect(redirectedUrl("/"));
	}
}