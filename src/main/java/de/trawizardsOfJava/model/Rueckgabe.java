package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

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

    private boolean angenommen = false;

}
