package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BenutzerRepository extends CrudRepository<Person, String> {
	Optional<Person> findByBenutzername(String benutzername);
}
