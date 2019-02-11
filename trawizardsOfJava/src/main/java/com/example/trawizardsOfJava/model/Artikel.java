package com.example.trawizardsOfJava.model;

import com.sun.javafx.beans.IDProperty;
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
    Person verleiher;
    String name;
    String beschreibung;
    String standort;
    int preis;
    int kaution;
    Verfügbarkeit verfügbarkeit;
}
