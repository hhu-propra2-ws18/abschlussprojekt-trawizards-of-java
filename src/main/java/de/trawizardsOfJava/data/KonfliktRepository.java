package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Konflikt;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface KonfliktRepository extends CrudRepository<Konflikt, Long> {

    ArrayList<Konflikt> findAll();

    ArrayList<Konflikt> findAllByInBearbeitung(String zustand);
}
