package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.RueckgabeRepository;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Rueckgabe;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import de.trawizardsOfJava.proPay.ProPay;
import de.trawizardsOfJava.proPay.ProPaySchnittstelle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class AusleihController {
	private ArtikelRepository artikelRepository;
	private AusleiheRepository ausleiheRepository;
	private MessageRepository messageRepository;
	private RueckgabeRepository rueckgabeRepository;

	@Autowired
	public AusleihController(ArtikelRepository artikelRepository, AusleiheRepository ausleiheRepository,
							 MessageRepository messageRepository, RueckgabeRepository rueckgabeRepository) {
		this.artikelRepository = artikelRepository;
		this.ausleiheRepository = ausleiheRepository;
		this.messageRepository = messageRepository;
		this.rueckgabeRepository = rueckgabeRepository;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/account/{benutzername}/artikel/{id}/anfrage")
	@PreAuthorize("#benutzername == authentication.name")
	public String neueAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		Artikel artikel = artikelRepository.findById(id).get();
		ArrayList<Verfuegbarkeit> verfuegbarkeiten = getVerfuegbarkeiten(ausleiheRepository.findByArtikel(artikel));
		model.addAttribute("daten", verfuegbarkeiten);
		model.addAttribute("verfuegbar", artikel.getVerfuegbarkeit());
		return "ausleihe";
	}

	private ArrayList<Verfuegbarkeit> getVerfuegbarkeiten(ArrayList<Ausleihe> ausleihen) {
		ArrayList<Verfuegbarkeit> verfuegbarkeiten = new ArrayList<>();
		for (Ausleihe ausleihe : ausleihen) {
			verfuegbarkeiten.add(ausleihe.getVerfuegbarkeit());
		}
		return verfuegbarkeiten;
	}

	@PostMapping("/account/{benutzername}/artikel/{id}/anfrage")
	@PreAuthorize("#benutzername == authentication.name")
	public String speichereAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id, String daterange, Principal principal) {
		Ausleihe ausleihe = new Ausleihe(artikelRepository.findById(id).get(), new Verfuegbarkeit(daterange), principal.getName());
		if (!ProPaySchnittstelle.getEntity(principal.getName()).genuegendGeld(ausleihe.berechneGesamtPreis(), ausleiheRepository.findByAusleihenderAndAccepted(principal.getName(), false))) {
			model.addAttribute("error", true);
			return neueAnfrage(model, benutzername, id);
		}
		ausleiheRepository.save(ausleihe);
		messageRepository.save(new Message(ausleihe, "angefragt"));
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/ausleihenuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String ausleihenUebersicht(Model model, @PathVariable String benutzername) {
		model.addAttribute("ausleihen", ausleiheRepository.findByVerleiherName(benutzername));
		return "ausleihenUebersicht";
	}

	@PostMapping("/account/{benutzername}/ausleihenuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String verwalteAusleihen(Model model, @PathVariable String benutzername, String art, Long id) {
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		if ("angenommen".equals(art)) {
			ProPay.bezahlvorgang(ausleihe);
			ausleiheRepository.save(ausleihe);
			messageRepository.save(new Message(ausleihe, "angenommen"));
		} else {
			ausleiheRepository.delete(ausleihe);
			messageRepository.save(new Message(ausleihe, "abgelehnt"));
		}
		return ausleihenUebersicht(model, benutzername);
	}

	@GetMapping("/account/{benutzername}/ausgelieheneuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String leihenUebersicht(Model model, @PathVariable String benutzername, Principal principal) {
		model.addAttribute("ausleihen", ausleiheRepository.findByAusleihender(principal.getName()));
		return "ausgelieheneUebersicht";
	}

	@PostMapping("/account/{benutzername}/ausgelieheneuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String verwalteRueckgabe(Model model, @PathVariable String benutzername, Long id, Principal principal) {
		Rueckgabe rueckgabe = new Rueckgabe(ausleiheRepository.findById(id).get());
		rueckgabeRepository.save(rueckgabe);
		ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		messageRepository.save(new Message(rueckgabe, "angefragt"));
		return leihenUebersicht(model, benutzername, principal);
	}

	@GetMapping("/account/{benutzername}/zurueckgegebeneartikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String rueckgabenUebersicht(Model model, @PathVariable String benutzername, Principal principal) {
		model.addAttribute("ausleihen", rueckgabeRepository.findByVerleiherName(principal.getName()));
		return "zurueckgegebeneartikel";
	}

	@PostMapping("/account/{benutzername}/zurueckgegebeneartikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String rueckgabeAkzeptiert(Model model, @PathVariable String benutzername, Long id, Principal principal) {
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		rueckgabe.setAngenommen(true);
		rueckgabeRepository.save(rueckgabe);
		messageRepository.save(new Message(rueckgabe, "angenommen"));
		ProPaySchnittstelle.post("reservation/release/" + rueckgabe.getAusleihender() + "?reservationId=" + rueckgabe.getProPayID());
		return rueckgabenUebersicht(model, benutzername, principal);
	}
}
