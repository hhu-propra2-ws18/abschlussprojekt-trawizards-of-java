package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

  @Autowired
  AusleiheRepository ausleiheRepository;

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
	public String postAddItem(Artikel artikel, @RequestParam String daterange, @PathVariable String benutzername) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
		verfuegbarkeit.toVerfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		return "UebersichtsSeite";
	}

	@GetMapping("/artikel/{id}/anfrage")
	public String neueAnfrage(@PathVariable Long id, Model model) {
		model.addAttribute("id", id);
		return "ausleihe";
	}

	@PostMapping("/artikel/{id}/anfrage")
	public String speichereAnfrage(@PathVariable Long id, @RequestParam String daterange, Model model, Principal principal) {
		Artikel artikel = artikelRepository.findById(id).get();
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
		verfuegbarkeit.toVerfuegbarkeit(daterange);
		Ausleihe ausleihe = new Ausleihe();
		ausleihe.setVerfuegbarkeit(verfuegbarkeit);
		ausleihe.setArtikel(artikel);
		ausleihe.setAusleihender(principal.getName());
		ausleiheRepository.save(ausleihe);
		System.out.println(ausleihe);
		model.addAttribute("artikelDetail", artikel);
		return "artikelDetail";
	}


    @GetMapping("/account/{Benutzername}/ausleihenuebersicht")
    public String ausleihenuebersicht(Model model, Principal principal){
        ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByverleiherName(principal.getName());
		System.out.println(ausleihen);
        model.addAttribute("ausleihen",ausleihen);
        return "ausleihenuebersicht";
    }

    @GetMapping("/ausleihen/{id}")
    public String ausleihebestaetigt(@PathVariable Long id){
        Ausleihe ausleihe = ausleiheRepository.findById(id).get();
        ausleihe.setAccepted(true);
        return "";
    }

    @GetMapping("/remove/{id}")
    public String ausleiheabgelehnt(@PathVariable Long id){
        ausleiheRepository.delete(ausleiheRepository.findById(id).get());
        return "ausleihenuebersicht";
    }
}
