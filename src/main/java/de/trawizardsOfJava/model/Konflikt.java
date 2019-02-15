package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Entity
public class Konflikt {

    @Id
    @GeneratedValue
    private Long id;

    private String beschreibung;

    @OneToOne
    private Rueckgabe rueckgabe;

    private String inBearbeitung = "offen";

    private String bearbeitender;
}
