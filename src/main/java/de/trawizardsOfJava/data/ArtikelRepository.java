package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Artikel;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {
	ArrayList<Artikel> findAll();
	ArrayList<Artikel> findByVerleiherBenutzername(String benutzername);
	ArrayList<Artikel> findAllByArtikelNameContaining(String artikelName);
	Optional<Artikel> findById(Long id);
}