package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Konflikt {

    @Id
    @GeneratedValue
    private Long id;

    private String beschreibung;
    private Rueckgabe rueckgabe;
}
