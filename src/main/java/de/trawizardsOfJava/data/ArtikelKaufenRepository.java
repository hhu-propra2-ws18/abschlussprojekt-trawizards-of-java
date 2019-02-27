package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.ArtikelKaufen;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ArtikelKaufenRepository extends CrudRepository<ArtikelKaufen, Long> {
    ArrayList<ArtikelKaufen> findByVerkaeufer(String benutzername);

    ArrayList<ArtikelKaufen> findAllByArtikelNameContaining(String q);
}
