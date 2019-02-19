package de.trawizardsofjava.messenger.data;

import de.trawizardsofjava.messenger.model.Nachricht;
import de.trawizardsofjava.messenger.model.Person;
import de.trawizardsofjava.messenger.model.Session;
import de.trawizardsofjava.messenger.model.Teilnehmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class DataIni implements ServletContextInitializer {
	@Autowired
	PersonRepo personRepo;
	@Autowired
	SessionRepo sessionRepo;
	@Autowired
	NachrichtenRepo nachrichtenRepo;

	@Override
	public void onStartup(ServletContext servletContext) {
		System.out.println("Populating the database");
		Person person = new Person();
		person.setPersonName("Ocramir");
		personRepo.save(person);
		Person person2 = new Person();
		person2.setPersonName("LordVader");
		personRepo.save(person2);
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setPersonEins(person);
		teilnehmer.setPersonZwei(person2);
		Session session = new Session();
		session.setTeilnehmer(teilnehmer);
		sessionRepo.save(session);
		Nachricht nachricht = new Nachricht();
		nachricht.setSession(session);
		nachricht.setNachricht("Season each side of the pumpkin seeds with six and a half teaspoons of pickles.");
		nachricht.setAbsender(person);
		nachrichtenRepo.save(nachricht);
		System.out.println("Finished populating the database");

		System.out.println(personRepo.findByPersonName("Ocramir"));
		System.out.println(sessionRepo.findByTeilnehmer(teilnehmer));
		System.out.println(nachrichtenRepo.findById(new Long(1)).get());
	}
}
