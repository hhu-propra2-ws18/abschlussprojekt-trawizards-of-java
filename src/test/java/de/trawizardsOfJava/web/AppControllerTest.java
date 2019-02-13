package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static  org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(AppController.class)
public class AppControllerTest {


	@MockBean
	SecurityPersonenService securityPersonenService;

	@Autowired
	MockMvc mvc;

	@MockBean
	AusleiheRepository ausleiheRepository;

	@MockBean
	BenutzerRepository benutzerRepository;

	@MockBean
	ArtikelRepository artikelRepository;


	@Test
	public void isOk() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void loginBadCredentials() throws Exception {
		mvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "user1")
				.param("password", "password1")).andExpect(redirectedUrl("/login?error"));
	}

	/*
	@Test
	public void loginCorrectCredentials() throws Exception {
		Person person1 = new Person();
		person1.setBenutzername("test");
		person1.setEmail("test@mail.com");
		person1.setName("test");
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		person1.setPasswort(bCryptPasswordEncoder.encode("1234"));
		person1.setRolle("ROLE_USER");
		benutzerRepository.save(person1);

		when(mock(SecurityPersonenService.class))

		mvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test")
				.param("password", "1234")).andExpect(status().isOk());
	}
	*/
}