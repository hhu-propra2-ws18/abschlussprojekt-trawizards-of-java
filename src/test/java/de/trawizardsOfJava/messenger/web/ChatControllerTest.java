package de.trawizardsOfJava.messenger.web;

import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.messenger.data.NachrichtenRepo;
import de.trawizardsOfJava.messenger.data.SessionRepo;
import de.trawizardsOfJava.messenger.model.Nachricht;
import de.trawizardsOfJava.messenger.model.Session;
import de.trawizardsOfJava.messenger.model.Teilnehmer;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.security.SecurityPersonenService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ChatController.class)
public class ChatControllerTest {
	ChatController chatController = new ChatController();
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	NachrichtenRepo nachrichtenRepo;
	
	@MockBean
	SessionRepo sessionRepo;
	
	@MockBean
	BenutzerRepository benutzerRepository;
	
	@MockBean
	SecurityPersonenService securityPersonenService;
	
	private Session session = new Session(new Teilnehmer("foo", "bar"));
	
	private Person person = new Person();
	
	private ArrayList<Session> sessions = new ArrayList<>();
	
	private Nachricht nachricht = new Nachricht();
	
	@Before
	@Test
	public void arrange(){
		session.setId(Long.valueOf(1));
		sessions.add(session);
		person.setBenutzername("foo");
		person.setRolle("admin");
		person.setName("foo");
		person.setPasswort("1234");
		person.setEmail("foo@gmail.com");
		nachricht.setChat("test");
		nachricht.setAbsender(person);
		nachricht.setSession(session);
		nachricht.setGesendet(LocalDateTime.now());
		nachricht.setId(Long.valueOf(1));
	}
	
	@Test
	@WithMockUser(username = "foo")
	public void uebersicht() throws Exception {
		when(sessionRepo.findByTeilnehmer(new Teilnehmer("foo", "bar"))).thenReturn(session);
		mvc.perform(get("/messenger/foo/bar/start")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "foo")
	public void sessionChat() throws Exception {
		when(sessionRepo.findById(Long.valueOf(1))).thenReturn(Optional.of(session));
		when(benutzerRepository.findByBenutzername("foo")).thenReturn(Optional.of(person));
		mvc.perform(get("/messenger/foo/" + 1)).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "foo")
	public void allChats() throws Exception {
		mvc.perform(get("/messenger/foo")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "foo")
	public void reloadChat() throws Exception {
		when(sessionRepo.findById(Long.valueOf(1))).thenReturn(Optional.of(session));
		mvc.perform(get("/messenger/foo/" + 1 + "/reload")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "foo")
	public void sendMessage() throws Exception {
		when(sessionRepo.findById(Long.valueOf(1))).thenReturn(Optional.of(session));
		when(benutzerRepository.findByBenutzername("foo")).thenReturn(Optional.of(person));
		//mvc.perform(get("/messenger/foo/" + 1)).andExpect(status().isOk());
		chatController.nachrichtenRepo = nachrichtenRepo;
		mvc.perform(post("/messenger/foo/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "" + 1)
				.param("chat", "test"));
		verify(nachrichtenRepo).save(any(Nachricht.class));
	}
}