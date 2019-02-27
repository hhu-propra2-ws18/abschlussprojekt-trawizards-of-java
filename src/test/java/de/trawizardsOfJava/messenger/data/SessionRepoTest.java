package de.trawizardsOfJava.messenger.data;

import de.trawizardsOfJava.messenger.model.Session;
import de.trawizardsOfJava.messenger.model.Teilnehmer;
import de.trawizardsOfJava.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
public class SessionRepoTest {
	@Autowired
	SessionRepo sessionRepo;
	
	@Test
	public void speichereSession(){
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		
		Assert.assertEquals(0, sessionRepo.findAll().size());
		sessionRepo.save(session);
		Assert.assertEquals(1, sessionRepo.findAll().size());
	}
	
	@Test
	public void speichereSessions(){
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Person person3 = new Person();
		person3.setBenutzername("Benutzer3");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Teilnehmer teilnehmer2 = new Teilnehmer();
		teilnehmer2.setPersonZwei(person.getBenutzername());
		teilnehmer2.setPersonEins(person3.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		Session session2 = new Session();
		session2.setTeilnehmer(teilnehmer2);
		
		Assert.assertEquals(0, sessionRepo.findAll().size());
		sessionRepo.save(session);
		sessionRepo.save(session2);
		Assert.assertEquals(2, sessionRepo.findAll().size());
	}
	
	@Test
	public void findSessionsByPerson(){
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Person person3 = new Person();
		person3.setBenutzername("Benutzer3");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Teilnehmer teilnehmer2 = new Teilnehmer();
		teilnehmer2.setPersonZwei(person.getBenutzername());
		teilnehmer2.setPersonEins(person3.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		Session session2 = new Session();
		session2.setTeilnehmer(teilnehmer2);
		
		sessionRepo.save(session);
		sessionRepo.save(session2);
		Assert.assertEquals(2, sessionRepo.findByTeilnehmer_PersonEinsOrTeilnehmer_PersonZwei(person.getBenutzername(), person.getBenutzername()).size());
	}
	
	@Test
	public void findByTeilnehmer(){
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Person person3 = new Person();
		person3.setBenutzername("Benutzer3");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Teilnehmer teilnehmer2 = new Teilnehmer();
		teilnehmer2.setPersonZwei(person.getBenutzername());
		teilnehmer2.setPersonEins(person3.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		Session session2 = new Session();
		session2.setTeilnehmer(teilnehmer2);
		
		sessionRepo.save(session);
		sessionRepo.save(session2);
		Assert.assertEquals(session, sessionRepo.findByTeilnehmer(teilnehmer));
	}
}