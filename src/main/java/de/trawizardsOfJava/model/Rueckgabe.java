package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class Rueckgabe{

    @Id
    @GeneratedValue
    private Long id;

    private String ausleihender;
    @OneToOne
    private Artikel artikel;

    private Verfuegbarkeit verfuegbarkeit;

    private String verleiherName;

}