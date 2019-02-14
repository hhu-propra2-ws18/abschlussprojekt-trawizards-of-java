package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Rueckgabe;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface RueckgabeRepository extends CrudRepository<Rueckgabe,Long> {

    List<Rueckgabe> findAll();

    ArrayList<Rueckgabe> findByVerleiherName(String verleiherName);

}
