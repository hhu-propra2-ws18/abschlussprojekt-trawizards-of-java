package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class Ausleihe {

    @Id
    @GeneratedValue
    private Long id;

    private String ausleihender;
    @OneToOne
    private Artikel artikel;

    private Verfuegbarkeit verfuegbarkeit;

    private String verleiherName;

    private boolean accepted = false;

    public void setArtikel(Artikel artikel){
        this.artikel = artikel;
        verleiherName = artikel.getVerleiherBenutzername();
    }

    public Rueckgabe convertToRueckgabe(Person ausleihender, Person verleiher){
        Rueckgabe rueckgabe = new Rueckgabe();
        rueckgabe.setId(this.id);
        rueckgabe.setAusleihender(ausleihender);
        rueckgabe.setArtikel(this.artikel);
        rueckgabe.setVerfuegbarkeit(this.verfuegbarkeit);
        rueckgabe.setVerleiherName(verleiher);
        return rueckgabe;
    }
}
