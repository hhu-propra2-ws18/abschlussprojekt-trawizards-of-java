package de.trawizardsOfJava.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class Ausleihe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ausleihender;
    @OneToOne
    private Artikel artikel;
    private Verfuegbarkeit verfuegbarkeit;
    private String verleiherName;
    private boolean accepted = false;
    private int proPayId;

    public Ausleihe(){}

    public Ausleihe(Artikel artikel, Verfuegbarkeit verfuegbarkeit, String ausleihender) {
        this.ausleihender = ausleihender;
        this.artikel = artikel;
        this.verfuegbarkeit = verfuegbarkeit;
        this.verleiherName = artikel.getVerleiherBenutzername();
    }

    public int berechneGesamtPreis() {
      return this.artikel.getKaution() + (this.verfuegbarkeit.berechneZwischenTage() * this.artikel.getPreis());
    }

    public boolean faelligeAusleihe(){
        return this.verfuegbarkeit.getEndDate().isBefore(LocalDate.now()) && this.accepted;
    }
}
