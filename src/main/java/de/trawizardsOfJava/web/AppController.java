package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.*;
import de.trawizardsOfJava.proPay.ProPaySchnittstelle;
import de.trawizardsOfJava.proPay.Reservierung;
import de.trawizardsOfJava.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.security.Principal;
import java.util.List;
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
	private MessageRepository messageRepository;

  	@Autowired
  	private RueckgabeRepository rueckgabeRepository;

  	@Autowired
	private KonfliktRepository konfliktRepository;
	
	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if(principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/")
	public String uebersicht(Model model, Principal principal) {
		List<Artikel> alleArtikel = artikelRepository.findAll();
		model.addAttribute("artikel", alleArtikel);

		if(principal != null){
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
			model.addAttribute("disableSecondButton", true);
		}else{
			model.addAttribute("disableThirdButton", true);
		}
		return "artikelDetail";
	}

	@GetMapping("/detail/{id}/changeItem")
	public String changeItem(Model model, @PathVariable Long id, Principal principal){
		Artikel artikel = artikelRepository.findById(id).get();
		if (!principal.getName().equals(artikel.getVerleiherBenutzername())) {
			return "permissionDenied";
		}
		model.addAttribute("artikel", artikel);
		return "changeItem";
	}

	@PostMapping("/detail/{id}/changeItem")
	public String postChangeItem(Artikel artikel, @RequestParam String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(artikel.getVerleiherBenutzername());
		artikelRepository.save(artikel);
		return "backToTheFuture";
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
		person.setPasswort(SecurityConfig.encoder().encode(person.getPasswort()));
		person.setRolle("ROLE_USER");
		benutzerRepository.save((person));
		return "backToTheFuture";
	}

	@GetMapping("/anmeldung")
	public String anmelden(){
		return "anmeldung";
	}

	@GetMapping("/account/{benutzername}")
	public String accountansicht(Model model, @PathVariable String benutzername, Principal principal) {
		Person person = benutzerRepository.findByBenutzername(benutzername).get();
		model.addAttribute("person", person);
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(person.getBenutzername()));
		model.addAttribute("isUser", benutzername.equals(principal.getName()));
		model.addAttribute("proPay", ProPaySchnittstelle.getEntity(benutzername));
		return "benutzeransicht";
	}

	@PostMapping("/account/{benutzername}")
	public String kontoAufladen(@PathVariable String benutzername, int amount) {
		ProPaySchnittstelle.post("account/" + benutzername + "?amount=" + amount);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/bearbeitung")
	public String benutzerverwaltung(Model model, @PathVariable String benutzername, Principal principal) {
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
		return "benutzerverwaltung";
	}

	@PostMapping("/account/{benutzername}/bearbeitung")
	public String speicherAenderung(Person person) {
		benutzerRepository.save((person));
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/addItem")
	public String addItem(Model model, @PathVariable String benutzername, Principal principal) {
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Artikel newArtikel = new Artikel();
		model.addAttribute("artikel", newArtikel);
		return "addItem";
	}

	@PostMapping("/account/{benutzername}/addItem")
	public String postAddItem(Artikel artikel, @PathVariable String benutzername, @RequestParam String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/artikel/{id}/anfrage")
	public String neueAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Artikel artikel = artikelRepository.findById(id).get();
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByArtikel(artikel);
		ArrayList<Verfuegbarkeit> verfuegbarkeiten = new ArrayList<>();
		for (Ausleihe ausleihe : ausleihen) {
			verfuegbarkeiten.add(ausleihe.getVerfuegbarkeit());
		}
		model.addAttribute("daten", verfuegbarkeiten);
		return "ausleihe";
	}

	@PostMapping("/account/{benutzername}/artikel/{id}/anfrage")
	public String speichereAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id, String daterange, Principal principal) {
		//ToDo: Überprüfung ob Geld ausreicht
		Artikel artikel = artikelRepository.findById(id).get();
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		Ausleihe ausleihe = new Ausleihe();
		ausleihe.setVerfuegbarkeit(verfuegbarkeit);
		ausleihe.setArtikel(artikel);
		ausleihe.setVerleiherName(artikel.getVerleiherBenutzername());
		ausleihe.setAusleihender(principal.getName());
		int verfuegbaresGeld = ProPaySchnittstelle.getEntity(principal.getName()).berechneVerfuegbaresGeld();
		int gebrauchtesGeld = ausleihe.berechneGesamtPreis();
		ArrayList<Ausleihe> anfragen = ausleiheRepository.findByAusleihenderAndAccepted(principal.getName(), false);
		for (Ausleihe anfrage : anfragen) {
			gebrauchtesGeld += anfrage.berechneGesamtPreis();
		}
		if (!(verfuegbaresGeld >= gebrauchtesGeld)){
			model.addAttribute("error", true);
			return neueAnfrage(model, benutzername, id, principal);
		}
		ausleiheRepository.save(ausleihe);
		Message message = new Message();
		message.setAbsender(principal.getName());
		message.setEmpfaenger(artikel.getVerleiherBenutzername());
		message.setNachricht("Anfrage für " + artikel.getArtikelName());
		messageRepository.save(message);
		Message anfrage = new Message();
		anfrage.setAbsender("System");
		anfrage.setEmpfaenger(principal.getName());
		anfrage.setNachricht("Anfrage für " + artikel.getArtikelName() + " erfolgreich gestellt!");
		messageRepository.save(anfrage);
		return "backToTheFuture";
	}

    @GetMapping("/account/{benutzername}/ausleihenuebersicht")
    public String ausleihenuebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByVerleiherName(benutzername);
		model.addAttribute("ausleihen", ausleihen);
		return "ausleihenuebersicht";
    }

    @GetMapping("/account/{benutzername}/annahme/{id}")
    public String ausleihebestaetigt(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		ausleihe.setAccepted(true);
		ausleiheRepository.save(ausleihe);
		Message message = new Message();
		message.setAbsender(principal.getName());
		message.setEmpfaenger(ausleihe.getAusleihender());
		message.setNachricht("Anfrage für " + ausleihe.getArtikel().getArtikelName() + " angenommen");
		messageRepository.save(message);
		model.addAttribute("ausleihen", ausleiheRepository.findByVerleiherName(principal.getName()));
		int tage = ausleihe.getVerfuegbarkeit().berechneZwischenTage();
		ProPaySchnittstelle.post("account/" +  ausleihe.getAusleihender() + "/transfer/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getPreis() * tage);
		ProPaySchnittstelle.post("reservation/reserve/" + ausleihe.getAusleihender() + "/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getKaution());
		List<Reservierung> reservierungen = ProPaySchnittstelle.getEntity(ausleihe.getAusleihender()).getReservations();
		ausleihe.setProPayId(reservierungen.get(reservierungen.size() - 1).getId());
		ausleiheRepository.save(ausleihe);
		return "ausleihenuebersicht";
    }

    @GetMapping("/account/{benutzername}/remove/{id}")
    public String ausleiheabgelehnt(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		ausleiheRepository.delete(ausleihe);
		Message message = new Message();
		message.setAbsender(principal.getName());
		message.setEmpfaenger(ausleihe.getAusleihender());
		message.setNachricht("Anfrage für " + ausleihe.getArtikel().getArtikelName() + " abgelehnt");
		messageRepository.save(message);
		model.addAttribute("ausleihen", ausleiheRepository.findByVerleiherName(principal.getName()));
		return "ausleihenuebersicht";
    }

	@GetMapping("/account/{benutzername}/ausgelieheneuebersicht")
	public String leihenuebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByAusleihender(principal.getName());
		model.addAttribute("ausleihen", ausleihen);
		return "ausgelieheneuebersicht";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/{id}")
	public String zurueckgegeben(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Rueckgabe rueckgabe = new Rueckgabe(ausleiheRepository.findById(id).get());
		rueckgabeRepository.save(rueckgabe);
		Message message = new Message();
		message.setAbsender(principal.getName());
		message.setEmpfaenger(rueckgabe.getVerleiherName());
		message.setNachricht(rueckgabe.getArtikel().getArtikelName() + " zurückgegeben");
		messageRepository.save(message);
		ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		model.addAttribute("ausleihen", ausleiheRepository.findByAusleihender(principal.getName()));
		return "ausgelieheneuebersicht";
	}

	@GetMapping("/account/{benutzername}/zurueckgegebeneartikel")
	public String rueckgabenuebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		ArrayList<Rueckgabe> ausleihen = rueckgabeRepository.findByVerleiherName(principal.getName());
		model.addAttribute("ausleihen", ausleihen);
		return "zurueckgegebeneartikel";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/akzeptiert/{id}")
	public String rueckgabeakzeptiert(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		Message message = new Message();
		message.setAbsender(principal.getName());
		message.setEmpfaenger(rueckgabe.getVerleiherName());
		message.setNachricht("Rückgabe von " + rueckgabe.getArtikel().getArtikelName() + " akzeptiert");
		messageRepository.save(message);
		rueckgabeRepository.delete(rueckgabe);
		model.addAttribute("ausleihen", rueckgabeRepository.findByVerleiherName(principal.getName()));
		ProPaySchnittstelle.post("reservation/release/" + rueckgabe.getAusleihender() + "?reservationId=" + rueckgabe.getProPayId());
		return "zurueckgegebeneartikel";
	}

	@GetMapping("/account/{benutzername}/nachrichten")
	public String nachrichtenUebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		ArrayList<Message> messages = messageRepository.findByEmpfaenger(benutzername);
		model.addAttribute("messages", messages);
		return "nachrichtenUebersicht";
	}

	@GetMapping("/account/{benutzername}/konflikt/send/{id}")
	public String konfliktErstellen(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Konflikt konflikt = new Konflikt();
		konflikt.setRueckgabe(rueckgabeRepository.findById(id).get());
		model.addAttribute("konflikt", konflikt);
		return "konfliktErstellung";
	}

	@PostMapping("/account/{benutzername}/konflikt/send/{id}")
	public String konfliktAbsenden(@PathVariable String benutzername, Konflikt konflikt, Principal principal){
		konfliktRepository.save(konflikt);
		Message message = new Message();
		message.setNachricht(konflikt.getBeschreibung());
		message.setAbsender(principal.getName());
		message.setEmpfaenger("");
		messageRepository.save(message);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachricht/delete/{id}")
	private String messageDelete(Model model, @PathVariable Long id, @PathVariable String benutzername, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		messageRepository.delete(messageRepository.findById(id).get());
		model.addAttribute("messages", messageRepository.findByEmpfaenger(principal.getName()));
		return "nachrichtenUebersicht";
	}

	@GetMapping("/search")
	public String search(@RequestParam final String q, final Model model, Principal principal) {
		model.addAttribute("artikel",this.artikelRepository.findAllByArtikelNameContaining(q));
		model.addAttribute("query", q);
		if(principal != null){
			model.addAttribute("disableSecondButton", true);
		}else{
			model.addAttribute("disableThirdButton", true);
		}
		return "search";
	}
}