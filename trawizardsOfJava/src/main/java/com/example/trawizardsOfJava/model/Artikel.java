package com.example.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Artikel {
    @Id
    @GeneratedValue
    Long id;
    String verleiher; //TODO
    String name;
    String beschreibung;
    String standort;
    int preis;
    int kaution;
    Verfuegbarkeit verfuegbarkeit;
}
