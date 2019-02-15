package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.model.Rueckgabe;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface RueckgabeRepository extends CrudRepository<Rueckgabe,Long> {

    ArrayList<Rueckgabe> findAll();

    ArrayList<Rueckgabe> findByVerleiherName(Person verleiherName);

}
