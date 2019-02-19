package de.trawizardsofjava.messenger.data;

import de.trawizardsofjava.messenger.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepo extends CrudRepository<Person, String> {
	Person findByPersonName(String personName);
}
