package com.example.trawizardsOfJava.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Artikel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String verleiherName; //TODO
    String artikelName;
    String beschreibung;
    String standort;
    int preis;
    int kaution;
    Verfuegbarkeit verfuegbarkeit;
}
