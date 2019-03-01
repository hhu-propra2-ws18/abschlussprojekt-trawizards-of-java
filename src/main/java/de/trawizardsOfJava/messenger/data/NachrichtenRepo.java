package de.trawizardsOfJava.messenger.data;

import de.trawizardsOfJava.messenger.model.Nachricht;
import de.trawizardsOfJava.messenger.model.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface NachrichtenRepo extends CrudRepository<Nachricht, Long> {
	ArrayList<Nachricht> findBySession(Session session);

	ArrayList<Nachricht> findAll();
}
