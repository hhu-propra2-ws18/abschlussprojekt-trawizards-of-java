package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Rueckgabe{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Person ausleihender;

    @OneToOne
    private Artikel artikel;

    private Verfuegbarkeit verfuegbarkeit;

    @OneToOne
    private Person verleiherName;

}
