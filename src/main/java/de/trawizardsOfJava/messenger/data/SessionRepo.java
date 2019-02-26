package de.trawizardsOfJava.messenger.data;

import de.trawizardsOfJava.messenger.model.Session;
import de.trawizardsOfJava.messenger.model.Teilnehmer;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface SessionRepo extends CrudRepository<Session, Long> {
	Session findByTeilnehmer(Teilnehmer teilnehmer);
	ArrayList<Session> findAll();
	ArrayList<Session> findByTeilnehmer_PersonEinsOrTeilnehmer_PersonZwei(String person, String gleichePerson);
}
