package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;

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

    private int proPayId;

    public void setArtikel(Artikel artikel){
        this.artikel = artikel;
        verleiherName = artikel.getVerleiherBenutzername();
    }

    public Rueckgabe convertToRueckgabe(){
        Rueckgabe rueckgabe = new Rueckgabe();
        rueckgabe.setId(this.id);
        rueckgabe.setAusleihender(this.ausleihender);
        rueckgabe.setArtikel(this.artikel);
        rueckgabe.setVerfuegbarkeit(this.verfuegbarkeit);
        rueckgabe.setVerleiherName(this.verleiherName);
        return rueckgabe;
    }
}
