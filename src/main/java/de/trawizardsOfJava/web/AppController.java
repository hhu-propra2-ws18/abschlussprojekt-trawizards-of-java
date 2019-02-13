package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AppController {

	@Autowired
	BenutzerRepository benutzerRepository;

	@Autowired
	ArtikelRepository artikelRepository;

	@GetMapping("/")
	public String uebersicht(Model model, Principal principal) {

		List<Artikel> alleArtikel = artikelRepository.findAll();

		model.addAttribute("artikel", alleArtikel);
		if(principal != null){
			model.addAttribute("name", principal.getName());
		}

		return "uebersichtSeite";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {

		Optional<Artikel> artikel = artikelRepository.findById(id);


		model.addAttribute("artikelDetail", artikel.get());
		if(principal != null){
			model.addAttribute("name", principal.getName());
		}

		return "artikelDetail";
	}

	@GetMapping("/registrierung")
	public String registrierung(Model model) {
		model.addAttribute("person", new Person());
		return "Registrierung";
	}

	@PostMapping("/registrierung")
	public String speicherePerson(Person person) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		person.setPasswort(bCryptPasswordEncoder.encode(person.getPasswort()));
		person.setRolle("ROLE_USER");
		benutzerRepository.save((person));
		return "BackToTheFuture";
	}

	@GetMapping("/account/{benutzername}")
	public String accountansicht(Model model, @PathVariable String benutzername) {
		Person person = benutzerRepository.findByBenutzername(benutzername).get();
		model.addAttribute("person", person);
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(person.getBenutzername()));
		return "Benutzeransicht";
	}

	@GetMapping("/account/{benutzername}/bearbeitung")
	public String benutzerverwaltung(Model model, @PathVariable String benutzername, Principal principal) {
		if (principal.getName().equals(benutzername)) {
			model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
			return "Benutzerverwaltung";
		} else {
			return "PermissionDenied";
		}
	}

	@PostMapping("/account/{benutzername}/bearbeitung")
	public String speicherAenderung(Person person) {
		benutzerRepository.save((person));
		return "Benutzerverwaltung";
	}

	@GetMapping("/account/{benutzername}/addItem")
	public String addItem(Model model, @PathVariable String benutzername, Principal principal) {
		if (principal.getName().equals(benutzername)) {
			Artikel newArtikel = new Artikel();
			model.addAttribute("artikel", newArtikel);
			return "addItem";
		} else {
			return "PermissionDenied";
		}
	}

	@PostMapping("/account/{benutzername}/addItem")
	public String postAddItem(Artikel artikel, @PathVariable String benutzername, @RequestParam String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
		verfuegbarkeit.toVerfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		return "UebersichtsSeite";
	}

	@GetMapping("/artikel/{id}/anfrage")
	public String neueAnfrage(Model model, @PathVariable Long id) {
		model.addAttribute("id", id);
		return "ausleihe";
	}

	@PostMapping("/artikel/{id}/anfrage")
	public String speichereAnfrage(Model model, @PathVariable Long id, @RequestParam String daterange) {
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
		model.addAttribute("artikelDetail", artikel);
		return "artikelDetail";
	}

}
