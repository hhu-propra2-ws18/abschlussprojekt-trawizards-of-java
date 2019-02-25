package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
public class Kauf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String kaeufer;
    @OneToOne
    private Artikel artikel;
    private String verleiherName;
    private boolean accepted = false;
    private Long proPayId;

    public Kauf(Artikel artikel, String kaeufer){
        this.artikel = artikel;
        this.kaeufer = kaeufer;
        this.verleiherName = artikel.getVerleiherBenutzername();
    }
}
