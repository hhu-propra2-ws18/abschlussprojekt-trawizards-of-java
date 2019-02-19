package de.trawizardsofjava.messenger.data;

import de.trawizardsofjava.messenger.model.Session;
import de.trawizardsofjava.messenger.model.Teilnehmer;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepo extends CrudRepository<Session, Teilnehmer> {
	Session findByTeilnehmer(Teilnehmer teilnehmer);
}
