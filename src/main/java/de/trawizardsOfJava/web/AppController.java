package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
import de.trawizardsOfJava.model.*;
import de.trawizardsOfJava.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AppController {
	private BenutzerRepository benutzerRepository;
	private ArtikelRepository artikelRepository;
	private ArtikelKaufenRepository artikelKaufenRepository;
	private IMailService iMailservice;

	@Autowired
	public AppController(BenutzerRepository benutzerRepository, ArtikelRepository artikelRepository,
	                     ArtikelKaufenRepository artikelKaufenRepository,IMailService iMailservice) {
		this.benutzerRepository = benutzerRepository;
		this.artikelRepository = artikelRepository;
		this.artikelKaufenRepository = artikelKaufenRepository;
		this.iMailservice = iMailservice;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/")
	public String startseite(Model model, Principal principal) {
		model.addAttribute("artikel", artikelRepository.findAll());
		model.addAttribute("artikelKaufen", artikelKaufenRepository.findAllByVerkauftFalse());
		model.addAttribute("aktuelleSeite", "Startseite");
		model.addAttribute("angemeldet", principal != null);
		return "startseite";
	}

	@GetMapping("/search")
	public String suche(Model model, @RequestParam final String q, Principal principal) {
		model.addAttribute("artikel", artikelRepository.findAllByArtikelNameContaining(q));
		model.addAttribute("artikelKaufen", artikelKaufenRepository.findAllByArtikelNameContainingAndVerkauftFalse(q));
		model.addAttribute("query", q);
		model.addAttribute("aktuelleSeite", "Suche");
		model.addAttribute("angemeldet", principal != null);
		return "suche";
	}

	@GetMapping("/registrierung")
	public String registrierung(Model model) {
		model.addAttribute("person", new Person());
		model.addAttribute("angemeldet", false);
		model.addAttribute("aktuelleSeite", "Registrierung");
		return "registrierung";
	}

	@PostMapping("/registrierung")
	public String speicherPerson(Model model, Person person) {
		if (benutzerRepository.findByBenutzername(person.getBenutzername()).isPresent()) {
			model.addAttribute("error", true);
			return registrierung(model);
		}
		person.setPasswort(SecurityConfig.encoder().encode(person.getPasswort()));
		benutzerRepository.save((person));
		iMailservice.willkommensMail(person.getEmail());
		model.addAttribute("link", "anmeldung");
		return "backToTheFuture";
	}

	@GetMapping("/anmeldung")
	public String anmelden(Model model) {
		model.addAttribute("angemeldet", false);
		model.addAttribute("aktuelleSeite", "Anmeldung");
		return "anmeldung";
	}

	@GetMapping("/zugriffVerweigert")
	public String zugriffVerweigert() {
		return "zugriffVerweigert";
	}
}
