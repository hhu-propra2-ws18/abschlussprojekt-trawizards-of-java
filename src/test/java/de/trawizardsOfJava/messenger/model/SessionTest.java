package de.trawizardsOfJava.messenger.model;

import de.trawizardsOfJava.messenger.data.SessionRepo;
import de.trawizardsOfJava.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SessionTest {
	@Autowired
	SessionRepo sessionRepo;
	@Test
	public void isExisting_notExis() {
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		session.setId(session.isExisting(sessionRepo.findAll()));
		Assert.assertEquals(session.getId(), Long.valueOf(-1));
	}
	
	@Test
	public void isExisting_ExisReverse() {
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		sessionRepo.save(session);
		Teilnehmer revteilnehmer = new Teilnehmer();
		revteilnehmer.setPersonEins(person2.getBenutzername());
		revteilnehmer.setPersonZwei(person.getBenutzername());
		Session revSession = new Session();
		revSession.setTeilnehmer(revteilnehmer);
		revSession.setId(revSession.isExisting(sessionRepo.findAll()));
		Assert.assertNotEquals(revSession.getId(), Long.valueOf(-1));
	}
	
	@Test
	public void isExisting_Exis() {
		Person person = new Person();
		person.setBenutzername("Benutzer1");
		Person person2 = new Person();
		person2.setBenutzername("Benutzer2");
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person.getBenutzername());
		teilnehmer.setPersonZwei(person2.getBenutzername());
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		sessionRepo.save(session);
		Teilnehmer neuTeilnehmer = new Teilnehmer();
		neuTeilnehmer.setPersonEins(person.getBenutzername());
		neuTeilnehmer.setPersonZwei(person2.getBenutzername());
		Session revSession = new Session();
		revSession.setTeilnehmer(neuTeilnehmer);
		revSession.setId(revSession.isExisting(sessionRepo.findAll()));
		Assert.assertNotEquals(revSession.getId(), Long.valueOf(-1));
	}
}