package com.example.trawizardsOfJava.data;

import com.example.trawizardsOfJava.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BenutzerRepository extends CrudRepository<Person, String> {
	Optional<Person> findByBenutzername(String benutzername);
}
