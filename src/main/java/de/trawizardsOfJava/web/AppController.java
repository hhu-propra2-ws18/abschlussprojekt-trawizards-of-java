package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.IMailService;
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

import java.time.LocalDate;
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

  	@Autowired
	private IMailService iMailService;

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
	public String postChangeItem(Model model, Artikel artikel, @RequestParam String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(artikel.getVerleiherBenutzername());
		artikelRepository.save(artikel);
		model.addAttribute("link", "/detail/" + artikel.getId());
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
		model.addAttribute("link", "anmeldung");
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

		ArrayList<Ausleihe> ausgelieheneArtikel = ausleiheRepository.findByAusleihender(benutzername);
		for(Ausleihe ausleihe: ausgelieheneArtikel){
			if(ausleihe.getVerfuegbarkeit().getEndDate().isBefore(LocalDate.now()) && ausleihe.isAccepted()){
				model.addAttribute("message","true");
				model.addAttribute("artikelName", ausleihe.getArtikel().getArtikelName());
				model.addAttribute("verleiherName", ausleihe.getVerleiherName());
			}
		}
		return "benutzeransicht";
	}

	@PostMapping("/account/{benutzername}")
	public String kontoAufladen(Model model, @PathVariable String benutzername, int amount) {
		ProPaySchnittstelle.post("account/" + benutzername + "?amount=" + amount);
		model.addAttribute("link", "account/" + benutzername);

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
	public String speicherAenderung(Model model, Person person) {
		benutzerRepository.save((person));
		model.addAttribute("link", "account/" + person.getBenutzername());
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
	public String postAddItem(Model model, Artikel artikel, @PathVariable String benutzername, @RequestParam String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		model.addAttribute("link", "account/" + benutzername);
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
		Message message = new Message(principal.getName(), artikel.getVerleiherBenutzername(), "Anfrage für " + artikel.getArtikelName());
		messageRepository.save(message);
		Message anfrage = new Message("System", principal.getName(), "Anfrage für " + artikel.getArtikelName() + " erfolgreich gestellt!");
		messageRepository.save(anfrage);
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
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
		Message message = new Message(principal.getName(), ausleihe.getAusleihender(), "Anfrage für " + ausleihe.getArtikel().getArtikelName() + " angenommen");
		messageRepository.save(message);
		int tage = ausleihe.getVerfuegbarkeit().berechneZwischenTage();
		ProPaySchnittstelle.post("account/" +  ausleihe.getAusleihender() + "/transfer/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getPreis() * tage);
		ProPaySchnittstelle.post("reservation/reserve/" + ausleihe.getAusleihender() + "/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getKaution());
		List<Reservierung> reservierungen = ProPaySchnittstelle.getEntity(ausleihe.getAusleihender()).getReservations();
		ausleihe.setProPayId(reservierungen.get(reservierungen.size() - 1).getId());
		ausleiheRepository.save(ausleihe);
		model.addAttribute("link", "account/" + benutzername + "/ausleihenuebersicht");
		return "backToTheFuture";
    }

    @GetMapping("/account/{benutzername}/remove/{id}")
    public String ausleiheabgelehnt(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		ausleiheRepository.delete(ausleihe);
		Message message = new Message(principal.getName(), ausleihe.getAusleihender(), "Anfrage für " + ausleihe.getArtikel().getArtikelName() + " wurde abgelehnt");
		messageRepository.save(message);
		model.addAttribute("link", "account/" + benutzername + "/ausleihenuebersicht");
		return "backToTheFuture";
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
	public String zurueckgegeben(@PathVariable("id") Long id, @PathVariable("benutzername") String benutzername, Model model, Principal principal){
		if (!benutzername.equals(principal.getName())) {
			return "permissionDenied";
		}
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		rueckgabeRepository.save(new Rueckgabe(ausleihe));
		Message message = new Message(principal.getName(), ausleihe.getVerleiherName(), ausleihe.getArtikel().getArtikelName() + " wurde zurückgegeben");
		messageRepository.save(message);
		ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		model.addAttribute("link", "account/" + benutzername + "/ausgelieheneuebersicht");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/zurueckgegebeneartikel")
	public String rueckgabenuebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (!benutzername.equals(principal.getName())) {
			return "permissionDenied";
		}
		ArrayList<Rueckgabe> ausleihen = rueckgabeRepository.findByVerleiherName(principal.getName());
		model.addAttribute("ausleihen", ausleihen);
		model.addAttribute("name", principal.getName());
		return "zurueckgegebeneartikel";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/akzeptiert/{id}")
	public String rueckgabeakzeptiert(@PathVariable("id") Long id, @PathVariable("benutzername") String benutzername, Model model, Principal principal){
		if (!benutzername.equals(principal.getName())) {
			return "permissionDenied";
		}
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		Message message = new Message(principal.getName(), rueckgabe.getVerleiherName(), "Rückgabe von " + rueckgabe.getArtikel().getArtikelName() + " akzeptiert");
		messageRepository.save(message);
		rueckgabe.setAngenommen(true);

		rueckgabeRepository.save(rueckgabe);
		model.addAttribute("link", "account/" + benutzername + "/zurueckgegebeneartikel");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachrichten")
	public String nachrichtenUebersicht(Model model, @PathVariable String benutzername, Principal principal){
		if (!benutzername.equals(principal.getName())) {
			return "permissionDenied";
		}
		if(benutzerRepository.findByBenutzername(benutzername).get().getRolle().equals("ROLE_ADMIN")){
			model.addAttribute("admin",true);
		}
		else model.addAttribute("admin",false);

		model.addAttribute("messages", messageRepository.findByEmpfaenger(benutzername));
		model.addAttribute("name", principal.getName());
		return "nachrichtenUebersicht";
	}

	@GetMapping("/account/{benutzername}/konflikt/send/{id}")
	public String konfliktErstellen(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Konflikt konflikt = new Konflikt();
		model.addAttribute("konflikt", konflikt);
		return "konfliktErstellung";
	}

	@PostMapping("/account/{benutzername}/konflikt/send/{id}")
	public String konfliktAbsenden(Konflikt konflikt, @PathVariable("benutzername") String benutzername, @PathVariable("id") Long id, Principal principal, Model model){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		konflikt.setAbsenderMail(benutzerRepository.findByBenutzername(benutzername).get().getEmail());
		konflikt.setVerursacherMail(benutzerRepository.findByBenutzername(rueckgabeRepository.findById(id).get().getAusleihender()).get().getEmail());
		konflikt.setRueckgabe(rueckgabeRepository.findById(id).get());
		konfliktRepository.save(konflikt);
		Message message = new Message(principal.getName(), "", konflikt.getBeschreibung());
		messageRepository.save(message);
		//iMailService.sendEmailToKonfliktLoeseStelle(benutzername,konflikt.getBeschreibung(),id);
		model.addAttribute("link", "account/" + benutzername + "/nachricht/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachricht/delete/{id}")
	private String messageDelete(Model model, @PathVariable Long id, @PathVariable String benutzername, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		messageRepository.delete(messageRepository.findById(id).get());
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
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

	@GetMapping("/account/{benutzername}/nachricht/konflikte")
	public String konfliktUebersicht(@PathVariable String benutzername, Model model, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		model.addAttribute("konflikte", konfliktRepository.findAllByInBearbeitung("offen"));
		model.addAttribute("konflikte1", konfliktRepository.findAllByBearbeitender(benutzername));
		model.addAttribute("name", benutzername);
		return "konfliktAnsicht";
	}

	@GetMapping("/account/{benutzername}/nachricht/konflikte/{id}")
	public String konfliktUebernehmen(@PathVariable("id") Long id, @PathVariable("benutzername") String benutzername, Model model, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Konflikt konflikt = konfliktRepository.findById(id).get();
		konflikt.setInBearbeitung("inBearbeitung");
		konflikt.setBearbeitender(benutzername);
		konfliktRepository.save(konflikt);
		model.addAttribute("link", "account/" + benutzername + "/nachricht/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachricht/konflikte/ausleihender/{id}")
	public String konfliktAusleihender(@PathVariable("id") Long id, @PathVariable("benutzername") String benutzername, Model model, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		//Kaution zurück an Ausleihenden
		Konflikt konflikt = konfliktRepository.findById(id).get();
		konflikt.setInBearbeitung("geschlossen");
		konflikt.setBearbeitender("");
		konfliktRepository.save(konflikt);
		Message message = new Message(benutzername, konfliktRepository.findById(id).get().getRueckgabe().getVerleiherName(), "Der Ausleihende erhält die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName() + "zurück");
		messageRepository.save(message);
		Message message1 = new Message(benutzername, konfliktRepository.findById(id).get().getRueckgabe().getAusleihender(), "Sie erhalten die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName() + "zurück");
		messageRepository.save(message1);

		Rueckgabe rueckgabe = konflikt.getRueckgabe();
		rueckgabe.setAngenommen(true);
		rueckgabeRepository.save(rueckgabe);

		model.addAttribute("link", "account/" + benutzername + "/nachricht/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachricht/konflikte/verleiher/{id}")
	public String konfliktVerleiher(@PathVariable("id") Long id, @PathVariable("benutzername") String benutzername, Model model, Principal principal){
		if (!principal.getName().equals(benutzername)) {
			return "permissionDenied";
		}
		Konflikt konflikt = konfliktRepository.findById(id).get();
		konflikt.setInBearbeitung("geschlossen");
		konflikt.setBearbeitender("");
		konfliktRepository.save(konflikt);
		Message message = new Message(benutzername, konfliktRepository.findById(id).get().getRueckgabe().getVerleiherName(), "Sie behalten die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName());
		messageRepository.save(message);
		Message message1 = new Message(benutzername, konfliktRepository.findById(id).get().getRueckgabe().getAusleihender(), "Der Verleiher behält die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName());
		messageRepository.save(message1);

		Rueckgabe rueckgabe = konflikt.getRueckgabe();
		rueckgabe.setAngenommen(true);
		rueckgabeRepository.save(rueckgabe);

		model.addAttribute("link", "account/" + benutzername + "/nachricht/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/transaktionUebersicht")
	public String transaktionen(@PathVariable String benutzername, Principal principal, Model model) {
		if(!principal.getName().equals(benutzername)){
			return "permissionDenied";
		}

		model.addAttribute("name", principal.getName());


		for(Rueckgabe rueckgabe: rueckgabeRepository.findAll()){
			if(rueckgabe.isAngenommen()){
				model.addAttribute("artikel", rueckgabeRepository.findByVerleiherName(principal.getName()));
				model.addAttribute("artikelAusgeliehen", rueckgabeRepository.findByAusleihender(principal.getName()));
			}
		}

		return "transaktionenUebersicht";
	}
}
