package de.trawizardsofjava.messenger.data;

import de.trawizardsofjava.messenger.model.Nachricht;
import de.trawizardsofjava.messenger.model.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface NachrichtenRepo extends CrudRepository<Nachricht, Long> {
    ArrayList<Nachricht> findBySession(Session session);
    ArrayList<Nachricht> findAll();
}
