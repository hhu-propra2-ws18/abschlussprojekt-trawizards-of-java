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

    private Verfuegbarkeit verfuegbarkeit;

/*
    public void accept(){
        //Artikel nicht mehr verfügbar
        //Kaution wird gezahlt
        //Zahlungsprozess beginnt
        //Anfragender wird benachrichtigt
    }

    public void decline(){
        //Anfrage wird entfernt
        //Anfragender wird benachrichtigt
    }

    public void giveBack(){
        //Artikel wieder verfügbar
        //Zahlungsprozess endet
        //über Kaution entschieden
        //Ausleihender wird benachrichtigt
    }
*/
}
