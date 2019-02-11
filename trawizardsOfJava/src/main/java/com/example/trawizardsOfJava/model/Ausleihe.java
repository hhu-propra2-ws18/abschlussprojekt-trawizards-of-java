package com.example.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Ausleihe {

    @Id
    @GeneratedValue
    private Long id;

    private String ausleihender;

    private Artikel artikel;

    private Zeitraum zeitraum;

}
