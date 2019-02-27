package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Bewertung;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;


public interface BewertungRepository extends CrudRepository<Bewertung, Long> {
	ArrayList<Bewertung> findAll();
	ArrayList<Bewertung> findByBewertungFuer(String bewertungFuer);
}
