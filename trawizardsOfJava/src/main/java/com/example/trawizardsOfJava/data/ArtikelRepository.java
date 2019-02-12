package com.example.trawizardsOfJava.data;

import org.springframework.stereotype.Repository;

@Repository
public interface ArtikelRepository extends CrudRepository<Artikel, Long> {
    ArrayList<Artikel> findAll();
}

