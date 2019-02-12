package com.example.trawizardsOfJava.web;

import com.example.trawizardsOfJava.data.ArtikelRepository;
import com.example.trawizardsOfJava.model.Ausleihe;
import com.example.trawizardsOfJava.model.Zeitraum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AppController {

    @Autowired
    ArtikelRepository artikelRepository;

    @GetMapping("/artikel/{id}/anfrage")
    public String neueAnfrage(){
        return "ausleihe";
    }

    @PostMapping("/artikel/{id}/anfrage")
    public String speichereAnfrage(@PathVariable("id") Long id, @RequestParam LocalDate startdate, @RequestParam LocalDate enddate){
        Zeitraum zeitraum = new Zeitraum(startdate,enddate);
        Ausleihe ausleihe = new Ausleihe();
        ausleihe.setZeitraum(zeitraum);
        ausleihe.setArtikel(artikelRepository.findById(id));
        //ausleihe.setAusleihender();
        return "/artikel/uebersicht";
    }
}
