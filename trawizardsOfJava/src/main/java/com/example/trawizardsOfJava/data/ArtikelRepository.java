package com.example.trawizardsOfJava.data;

import com.example.trawizardsOfJava.model.Artikel;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {

    List<Artikel> findAll();
    //TODO
    ArrayList<Artikel> findByVerleiher(String person);
}
