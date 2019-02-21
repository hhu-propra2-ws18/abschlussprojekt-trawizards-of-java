package de.trawizardsofjava.messenger.data;

import de.trawizardsofjava.messenger.model.Session;
import de.trawizardsofjava.messenger.model.Teilnehmer;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface SessionRepo extends CrudRepository<Session, Long> {
	Session findByTeilnehmer(Teilnehmer teilnehmer);
	ArrayList<Session> findAll();
}
