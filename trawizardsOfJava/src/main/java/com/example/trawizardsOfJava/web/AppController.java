package com.example.trawizardsOfJava.web;

import com.example.trawizardsOfJava.data.ArtikelRepository;
import com.example.trawizardsOfJava.model.Artikel;
import com.example.trawizardsOfJava.model.Verfuegbarkeit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AppController {
    @Autowired
    ArtikelRepository artikelRepository;

    @GetMapping("/Benutzer/addItem")
    public String addItem(Model model){
        Artikel newArtikel= new Artikel();
        Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
        newArtikel.setVerfuegbarkeit(verfuegbarkeit);
        model.addAttribute("artikel", newArtikel);
        return "addItem";
    }

    @PostMapping("/Benutzer/addItem")
    public String postAddItem(Model model, Artikel artikel, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){

        artikelRepository.save(artikel);
        return ansichtItems(model);
    }

    @GetMapping("/Benutzer/Items")
    private String ansichtItems(Model model) {
        artikelRepository.findByVerleiher("Udo"); //TODO
        return "ansicht";
    }
}
