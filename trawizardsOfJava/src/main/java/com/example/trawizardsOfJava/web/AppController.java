package com.example.trawizardsOfJava.web;

import com.example.trawizardsOfJava.data.ArtikelRepository;
import com.example.trawizardsOfJava.model.Artikel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppController {
    @Autowired
    ArtikelRepository artikelRepository;

    @GetMapping("/Benutzer/addItem")
    public String addItem(Model model){
        Artikel newArtikel= new Artikel();
        model.addAttribute("artikel", newArtikel);
        return "addItem";
    }

    @PostMapping("/Benutzer/addItem")
    public String postAddItem(Model model, Artikel artikel){
        artikelRepository.save(artikel);
        return ansichtItems(model);
    }

    @GetMapping("/Benutzer/Items")
    private String ansichtItems(Model model) {
        artikelRepository.findBy
    }
}
