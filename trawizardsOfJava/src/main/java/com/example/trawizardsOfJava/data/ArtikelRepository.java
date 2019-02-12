package com.example.trawizardsOfJava.data;

import com.example.trawizardsOfJava.model.Artikel;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {
    ArrayList<Artikel> findAll();
    //TODO
    ArrayList<Artikel> findByVerleiher(String person);
}
