package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    BenutzerRepository benutzerRepository;

    @Autowired
    ArtikelRepository artikelRepository;

    @GetMapping("/")
    public String uebersicht(Model model) {

        List<Artikel> alleArtikel = artikelRepository.findAll();

        model.addAttribute("artikel", alleArtikel);

        return "uebersichtSeite";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Long id) {

        Optional<Artikel> artikel = artikelRepository.findById(id);


        model.addAttribute("artikelDetail", artikel.get());

        return "artikelDetail";
    }

    @GetMapping("/benutzerverwaltung/{benutzername}")
    public String benutzerverwaltung(Model model, @PathVariable String benutzername) {
        model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
        return "Benutzerverwaltung";
    }

    @PostMapping("/benutzerverwaltung/{benutzername}")
    public String speicherAenderung(Person person) {
        benutzerRepository.save((person));
        return "Benutzerverwaltung";
    }

    @GetMapping("/registrierung")
    public String registrierung(Model model) {
        model.addAttribute("person", new Person());
        return "Registrierung";
    }

    @PostMapping("/registrierung")
    public String speicherePerson(Person person) {
        benutzerRepository.save((person));
        return "BackToTheFuture";
    }

    @GetMapping("/artikel/{id}/anfrage")
    public String neueAnfrage(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "ausleihe";
    }

    @PostMapping("/artikel/{id}/anfrage")
    public String speichereAnfrage(@PathVariable Long id, @RequestParam String daterange) {
        Artikel artikel = artikelRepository.findById(id).get();
        Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
        verfuegbarkeit.toVerfuegbarkeit(daterange);
        artikel.setVerfuegbarkeit(verfuegbarkeit);
        System.out.println(artikel);
        //Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
        //verfuegbarkeit.setStartDate(startdate);
        //verfuegbarkeit.setEndDate(enddate);
        //Ausleihe ausleihe = new Ausleihe();
        //ausleihe.setVerfuegbarkeit(verfuegbarkeit);
        //ausleihe.setArtikel(artikelRepository.findById(id).get());
        //ausleihe.setAusleihender();
        return "artikelDetail";
    }

    @GetMapping("/Benutzer/addItem")
    public String addItem(Model model) {
        Artikel newArtikel = new Artikel();
        model.addAttribute("artikel", newArtikel);
        return "addItem";
    }

    @PostMapping("/Benutzer/addItem")
    public String postAddItem(Model model, Artikel artikel, @RequestParam String daterange) {
        Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
        verfuegbarkeit.toVerfuegbarkeit(daterange);
        artikel.setVerfuegbarkeit(verfuegbarkeit);
        artikel.setVerleiherName(benutzerRepository.findByBenutzername("Ocramir").get()); //TODO Verleiher
        artikelRepository.save(artikel);
        return ansichtItems(model);
    }

    @GetMapping("/Benutzer/Items")
    private String ansichtItems(Model model) {
        artikelRepository.findByverleiherName(benutzerRepository.findByBenutzername("Ocramir").get()); //TODO
        return uebersicht(model);
    }
}
