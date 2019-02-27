package de.trawizardsOfJava.messenger.data;

import de.trawizardsOfJava.messenger.model.Nachricht;
import de.trawizardsOfJava.messenger.model.Session;
import de.trawizardsOfJava.messenger.model.Teilnehmer;
import de.trawizardsOfJava.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NachrichtenRepoTest {
	@Autowired
	NachrichtenRepo nachrichtenRepo;
	@Autowired
	SessionRepo sessionRepo;
	
	@Test
	public void speichere(){
		Person person = new Person();
		person.setEmail("Benutzer1@gmx.de");
		person.setBenutzername("Benutzer1");
		person.setPasswort("1234");
		person.setName("Benutzer1");
		person.setRolle("Admin");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		sessionRepo.save(session);
		Nachricht nachricht = new Nachricht();
		nachricht.setSession(session);
		nachricht.setAbsender(person);
		nachricht.setGesendet(LocalDateTime.now());
		nachricht.setChat("test");
		
		nachrichtenRepo.save(nachricht);
		Assert.assertEquals(1, nachrichtenRepo.findAll().size());
	}
	@Test
	public void findBySession(){
		Person person = new Person();
		person.setEmail("Benutzer1@gmx.de");
		person.setBenutzername("Benutzer1");
		person.setPasswort("1234");
		person.setName("Benutzer1");
		person.setRolle("Admin");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		sessionRepo.save(session);
		Nachricht nachricht = new Nachricht();
		nachricht.setSession(session);
		nachricht.setAbsender(person);
		nachricht.setGesendet(LocalDateTime.now());
		nachricht.setChat("test");
		
		nachrichtenRepo.save(nachricht);
		Assert.assertEquals(1, nachrichtenRepo.findBySession(session).size());
	}
}