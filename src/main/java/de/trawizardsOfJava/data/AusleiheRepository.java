package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface AusleiheRepository extends CrudRepository<Ausleihe,Long> {

    List<Ausleihe> findAll();

    ArrayList<Ausleihe> findByVerleiherName(String verleiherName);

    ArrayList<Ausleihe> findByArtikel(Artikel artikel);

    ArrayList<Ausleihe> findByAusleihender(String name);
}
