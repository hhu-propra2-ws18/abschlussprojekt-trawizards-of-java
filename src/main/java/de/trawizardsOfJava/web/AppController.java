package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.data.RueckgabeRepository;
import de.trawizardsOfJava.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AppController {

	@Autowired
	private BenutzerRepository benutzerRepository;

	@Autowired
	private ArtikelRepository artikelRepository;

  	@Autowired
  	private AusleiheRepository ausleiheRepository;

  	@Autowired
  	private RueckgabeRepository rueckgabeRepository;

	@GetMapping("/")
	public String uebersicht(Model model, Principal principal) {
		List<Artikel> alleArtikel = artikelRepository.findAll();
		model.addAttribute("artikel", alleArtikel);

		if(principal != null){
			model.addAttribute("name", principal.getName());
			model.addAttribute("disableSecondButton", true);
		}else{
			model.addAttribute("disableThirdButton", true);
		}
		return "uebersichtSeite";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		Optional<Artikel> artikel = artikelRepository.findById(id);
		model.addAttribute("artikelDetail", artikel.get());
		if(principal != null){
			model.addAttribute("name", principal.getName());
			model.addAttribute("disableSecondButton", true);
		}else{
			model.addAttribute("disableThirdButton", true);
		}
		return "artikelDetail";
	}

	@GetMapping("/registrierung")
	public String registrierung(Model model) {
		model.addAttribute("person", new Person());
		return "registrierung";
	}

	@PostMapping("/registrierung")
	public String speicherePerson(Model model, Person person) {
		if (benutzerRepository.findByBenutzername(person.getBenutzername()).isPresent()){
			model.addAttribute("error", true);
			return "registrierung";
		}
		else {
			person.setPasswort(SecurityConfig.encoder().encode(person.getPasswort()));
			person.setRolle("ROLE_USER");
			benutzerRepository.save((person));
			return "backToTheFuture";
		}
	}

	@GetMapping("/account/{benutzername}")
	public String accountansicht(Model model, @PathVariable String benutzername, Principal principal) {
		Person person = benutzerRepository.findByBenutzername(benutzername).get();
		model.addAttribute("person", person);
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(person.getBenutzername()));
		model.addAttribute("isUser", benutzername.equals(principal.getName()));
		model.addAttribute("proPay", ControllerLogik.getEntity(benutzername));
		return "benutzeransicht";
	}

	@PostMapping("/account/{benutzername}")
	public String kontoAufladen(@PathVariable String benutzername, int amount) {
		ControllerLogik.setAmount(benutzername, amount);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/bearbeitung")
	public String benutzerverwaltung(Model model, @PathVariable String benutzername, Principal principal) {
		if (principal.getName().equals(benutzername)) {
			model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
			return "benutzerverwaltung";
		} else {
			return "permissionDenied";
		}
	}

	@PostMapping("/account/{benutzername}/bearbeitung")
	public String speicherAenderung(Person person) {
		benutzerRepository.save((person));
		return "benutzerverwaltung";
	}

	@GetMapping("/account/{benutzername}/addItem")
	public String addItem(Model model, @PathVariable String benutzername, Principal principal) {
		if (principal.getName().equals(benutzername)) {
			Artikel newArtikel = new Artikel();
			model.addAttribute("artikel", newArtikel);
			model.addAttribute("name", principal.getName());
			return "addItem";
		} else {
			return "permissionDenied";
		}
	}

	@PostMapping("/account/{benutzername}/addItem")
	public String postAddItem(Artikel artikel, @PathVariable String benutzername, @RequestParam String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
		verfuegbarkeit.toVerfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		return "backToTheFuture";
	}

	@GetMapping("/artikel/{id}/anfrage")
	public String neueAnfrage(Model model, @PathVariable Long id, Principal principal) {
		model.addAttribute("id", id);
		Artikel artikel =  artikelRepository.findById(id).get();
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByArtikel(artikel);
		ArrayList<Verfuegbarkeit> verbuebarkeiten = new ArrayList<>();
		for (Ausleihe ausleihe: ausleihen){
			verbuebarkeiten.add(ausleihe.getVerfuegbarkeit());
		}
		model.addAttribute("daten", verbuebarkeiten);
		model.addAttribute("name", principal.getName());
		return "ausleihe";
	}

	@PostMapping("/artikel/{id}/anfrage")
	public String speichereAnfrage(@PathVariable Long id, @RequestParam String daterange, Principal principal) {
		Artikel artikel = artikelRepository.findById(id).get();
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
		verfuegbarkeit.toVerfuegbarkeit(daterange);
		Ausleihe ausleihe = new Ausleihe();
		ausleihe.setVerfuegbarkeit(verfuegbarkeit);
		ausleihe.setArtikel(artikel);
		ausleihe.setAusleihender(principal.getName());
		ausleiheRepository.save(ausleihe);
		return "backToTheFuture";
	}


    @GetMapping("/account/{benutzername}/ausleihenuebersicht")
    public String ausleihenuebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (benutzername.equals(principal.getName())) {
			ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByVerleiherName(benutzername);
			model.addAttribute("ausleihen", ausleihen);
			model.addAttribute("name", principal.getName());
			return "ausleihenuebersicht";
		}
		else {
			return "permissionDenied";
		}
    }

    @GetMapping("/annahme/{id}")
    public String ausleihebestaetigt(@PathVariable Long id, Model model, Principal principal){
        Ausleihe ausleihe = ausleiheRepository.findById(id).get();
        ausleihe.setAccepted(true);
        ausleiheRepository.save(ausleihe);
        model.addAttribute("ausleihen", ausleiheRepository.findByVerleiherName(principal.getName()));
		model.addAttribute("name", principal.getName());
        return "ausleihenuebersicht";
    }

    @GetMapping("/remove/{id}")
    public String ausleiheabgelehnt(@PathVariable Long id, Model model, Principal principal){
        ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		model.addAttribute("ausleihen", ausleiheRepository.findByVerleiherName(principal.getName()));
		model.addAttribute("name", principal.getName());
        return "ausleihenuebersicht";
    }

	@GetMapping("/account/{benutzername}/ausgelieheneuebersicht")
	public String leihenuebersicht(Model model, Principal principal){
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByAusleihender(principal.getName());
		model.addAttribute("name", principal.getName());
		model.addAttribute("ausleihen",ausleihen);
		return "ausgelieheneuebersicht";
	}

	@GetMapping("/rueckgabe/{id}")
	public String zurueckgegeben(@PathVariable Long id, Model model, Principal principal){
		rueckgabeRepository.save(ausleiheRepository.findById(id).get().convertToRueckgabe());
		ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		model.addAttribute("ausleihen", ausleiheRepository.findByAusleihender(principal.getName()));
		return "ausgelieheneuebersicht";
	}

	@GetMapping("/account/{benutzername}/zurueckgegebeneartikel")
	public String rueckgabenuebersicht(Model model, Principal principal){
		ArrayList<Rueckgabe> ausleihen = rueckgabeRepository.findByVerleiherName(principal.getName());
		model.addAttribute("ausleihen",ausleihen);
		model.addAttribute("name", principal.getName());
		return "zurueckgegebeneartikel";
	}

	@GetMapping("/rueckgabe/akzeptiert/{id}")
	public String rueckgabeakzeptiert(@PathVariable Long id, Model model, Principal principal){
		rueckgabeRepository.delete(rueckgabeRepository.findById(id).get());
		model.addAttribute("ausleihen", rueckgabeRepository.findByVerleiherName(principal.getName()));
		return "zurueckgegebeneartikel";
	}



}
