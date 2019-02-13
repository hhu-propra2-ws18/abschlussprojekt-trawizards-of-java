package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {

    List<Artikel> findAll();
    //TODO
    ArrayList<Artikel> findByVerleiherBenutzername(String benutzername);
}
