package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Kauf;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface KaufRepository extends CrudRepository<Kauf, Long> {

	ArrayList<Kauf> findByVerkaeufer(String benutzername);

	ArrayList<Kauf> findByKaeufer(String benutzername);
}
