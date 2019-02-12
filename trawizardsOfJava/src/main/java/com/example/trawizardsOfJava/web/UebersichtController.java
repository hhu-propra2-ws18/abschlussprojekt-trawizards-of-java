package com.example.trawizardsOfJava.web;


import com.example.trawizardsOfJava.data.ArtikelRepository;
import com.example.trawizardsOfJava.model.Artikel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UebersichtController {

    @Autowired
    ArtikelRepository artikelRepository;

    @GetMapping("/")
    public String uebersicht(Model model){

        List<Artikel> alleArtikel = artikelRepository.findAll();

        model.addAttribute("artikel", alleArtikel);

        return "uebersichtSeite";
    }
}
