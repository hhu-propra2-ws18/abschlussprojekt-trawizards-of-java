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
import org.springframework.security.access.prepost.PreAuthorize;
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
	
	@Autowired
	private IMailService iMailService;
	
	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/")
	public String uebersicht(Model model, Principal principal) {
		List<Artikel> alleArtikel = artikelRepository.findAll();
		model.addAttribute("artikel", alleArtikel);
		model.addAttribute("aktuelleSeite", "Startseite");
		if (principal != null) {
			model.addAttribute("angemeldet", true);
		} else {
			model.addAttribute("angemeldet", false);
		}
		return "uebersichtSeite";
	}

	@GetMapping("/search")
	public String search(Model model, @RequestParam final String q, Principal principal) {
		model.addAttribute("artikel", this.artikelRepository.findAllByArtikelNameContaining(q));
		model.addAttribute("query", q);
		model.addAttribute("aktuelleSeite", "Suche");
		if (principal != null) {
			model.addAttribute("angemeldet", true);
		} else {
			model.addAttribute("angemeldet", false);
		}
		return "search";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		Optional<Artikel> artikel = artikelRepository.findById(id);
		model.addAttribute("artikelDetail", artikel.get());
		model.addAttribute("aktuelleSeite", "Artikelansicht");
		if (principal != null) {
			model.addAttribute("angemeldet", true);
		}
		else {
			model.addAttribute("angemeldet", false);
		}
		return "artikelDetail";
	}

	@GetMapping("/account/{benutzername}/aendereArtikel/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String changeItem(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		Artikel artikel = artikelRepository.findById(id).get();
		model.addAttribute("artikel", artikel);
		return "changeItem";
	}

	@PostMapping("/account/{benutzername}/aendereArtikel/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String postChangeItem(Model model, @PathVariable String benutzername, Artikel artikel, String daterange) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikelRepository.save(artikel);
		model.addAttribute("link", "detail/" + artikel.getId());
		return "backToTheFuture";
	}

	@GetMapping("/registrierung")
	public String registrierung(Model model) {
		model.addAttribute("person", new Person());
		model.addAttribute("angemeldet", false);
		model.addAttribute("aktuelleSeite", "Registrierung");
		return "registrierung";
	}

	@PostMapping("/registrierung")
	public String speicherePerson(Model model, Person person) {
		if (benutzerRepository.findByBenutzername(person.getBenutzername()).isPresent()) {
			model.addAttribute("error", true);
			return registrierung(model);
		}
		person.setPasswort(SecurityConfig.encoder().encode(person.getPasswort()));
		benutzerRepository.save((person));
		model.addAttribute("link", "anmeldung");
		return "backToTheFuture";
	}

	@GetMapping("/anmeldung")
	public String anmelden(Model model) {
		model.addAttribute("angemeldet", false);
		model.addAttribute("aktuelleSeite", "Anmeldung");
		return "anmeldung";
	}

	@GetMapping("/account/{benutzername}")
	public String accountansicht(Model model, @PathVariable String benutzername, Principal principal) {
		Person person = benutzerRepository.findByBenutzername(benutzername).get();
		model.addAttribute("person", person);
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(person.getBenutzername()));
		model.addAttribute("isUser", benutzername.equals(principal.getName()));
		model.addAttribute("proPay", ProPaySchnittstelle.getEntity(benutzername));
		model.addAttribute("angemeldet", true);
		model.addAttribute("aktuelleSeite", "Profil");

		ArrayList<Ausleihe> ausgelieheneArtikel = ausleiheRepository.findByAusleihender(benutzername);
		for (Ausleihe ausleihe : ausgelieheneArtikel) {
			if (ausleihe.getVerfuegbarkeit().getEndDate().isBefore(LocalDate.now()) && ausleihe.isAccepted()) {
				//iMailService.sendReminder(person.getEmail(),person.getName(), ausleihe.getArtikel().getArtikelName());
				model.addAttribute("message", "true");
				model.addAttribute("artikelName", ausleihe.getArtikel().getArtikelName());
				model.addAttribute("verleiherName", ausleihe.getVerleiherName());
			}
		}
		return "benutzeransicht";
	}

	@PostMapping("/account/{benutzername}")
	@PreAuthorize("#benutzername == authentication.name")
	public String kontoAufladen(Model model, @PathVariable String benutzername, int amount) {
		ProPaySchnittstelle.post("account/" + benutzername + "?amount=" + amount);
		model.addAttribute("link", "account/" + benutzername);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/bearbeitung")
	@PreAuthorize("#benutzername == authentication.name")
	public String benutzerverwaltung(Model model, @PathVariable String benutzername) {
		model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
		return "benutzerverwaltung";
	}

	@PostMapping("/account/{benutzername}/bearbeitung")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherAenderung(Model model, @PathVariable String benutzername, Person person) {
		benutzerRepository.save((person));
		model.addAttribute("link", "account/" + person.getBenutzername());
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/addItem")
	@PreAuthorize("#benutzername == authentication.name")
	public String addItem(Model model, @PathVariable String benutzername) {
		Artikel newArtikel = new Artikel();
		model.addAttribute("artikel", newArtikel);
		return "addItem";
	}

	@PostMapping("/account/{benutzername}/addItem")
	@PreAuthorize("#benutzername == authentication.name")
	public String postAddItem(Model model, @PathVariable String benutzername, String daterange, Artikel artikel) {
		Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit(daterange);
		artikel.setVerfuegbarkeit(verfuegbarkeit);
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		model.addAttribute("link", "account/" + benutzername);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/artikel/{id}/anfrage")
	@PreAuthorize("#benutzername == authentication.name")
	public String neueAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		Artikel artikel = artikelRepository.findById(id).get();
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByArtikel(artikel);
		ArrayList<Verfuegbarkeit> verfuegbarkeiten = new ArrayList<>();
		for (Ausleihe ausleihe : ausleihen) {
			verfuegbarkeiten.add(ausleihe.getVerfuegbarkeit());
		}
		model.addAttribute("daten", verfuegbarkeiten);
		model.addAttribute("verfuegbar", artikel.getVerfuegbarkeit());
		return "ausleihe";
	}

	@PostMapping("/account/{benutzername}/artikel/{id}/anfrage")
	@PreAuthorize("#benutzername == authentication.name")
	public String speichereAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id, String daterange, Principal principal) {
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
		if (!(verfuegbaresGeld >= gebrauchtesGeld)) {
			model.addAttribute("error", true);
			return neueAnfrage(model, benutzername, id);
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
	@PreAuthorize("#benutzername == authentication.name")
	public String ausleihenuebersicht(Model model, @PathVariable String benutzername) {
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByVerleiherName(benutzername);
		model.addAttribute("ausleihen", ausleihen);
		return "ausleihenuebersicht";
	}

	@GetMapping("/account/{benutzername}/annahme/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String ausleihebestaetigt(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		ausleihe.setAccepted(true);
		ausleiheRepository.save(ausleihe);
		Message message = new Message(principal.getName(), ausleihe.getAusleihender(), "Anfrage für " + ausleihe.getArtikel().getArtikelName() + " angenommen");
		messageRepository.save(message);
		int tage = ausleihe.getVerfuegbarkeit().berechneZwischenTage();
		ProPaySchnittstelle.post("account/" + ausleihe.getAusleihender() + "/transfer/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getPreis() * tage);
		ProPaySchnittstelle.post("reservation/reserve/" + ausleihe.getAusleihender() + "/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getKaution());
		List<Reservierung> reservierungen = ProPaySchnittstelle.getEntity(ausleihe.getAusleihender()).getReservations();
		ausleihe.setProPayId(reservierungen.get(reservierungen.size() - 1).getId());
		ausleiheRepository.save(ausleihe);
		model.addAttribute("link", "account/" + benutzername + "/ausleihenuebersicht");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/remove/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String ausleiheabgelehnt(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		ausleiheRepository.delete(ausleihe);
		Message message = new Message(principal.getName(), ausleihe.getAusleihender(), "Anfrage für " + ausleihe.getArtikel().getArtikelName() + " wurde abgelehnt");
		messageRepository.save(message);
		model.addAttribute("link", "account/" + benutzername + "/ausleihenuebersicht");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/ausgelieheneuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String leihenuebersicht(Model model, @PathVariable String benutzername, Principal principal) {
		ArrayList<Ausleihe> ausleihen = ausleiheRepository.findByAusleihender(principal.getName());
		model.addAttribute("ausleihen", ausleihen);
		return "ausgelieheneuebersicht";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String zurueckgegeben(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		rueckgabeRepository.save(new Rueckgabe(ausleihe));
		Message message = new Message(principal.getName(), ausleihe.getVerleiherName(), ausleihe.getArtikel().getArtikelName() + " wurde zurückgegeben");
		messageRepository.save(message);
		ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		model.addAttribute("link", "account/" + benutzername + "/ausgelieheneuebersicht");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/zurueckgegebeneartikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String rueckgabenuebersicht(Model model, @PathVariable String benutzername, Principal principal) {
		ArrayList<Rueckgabe> ausleihen = rueckgabeRepository.findByVerleiherName(principal.getName());
		model.addAttribute("ausleihen", ausleihen);
		return "zurueckgegebeneartikel";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/akzeptiert/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String rueckgabeakzeptiert(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		Message message = new Message(principal.getName(), rueckgabe.getVerleiherName(), "Rückgabe von " + rueckgabe.getArtikel().getArtikelName() + " akzeptiert");
		messageRepository.save(message);
		rueckgabe.setAngenommen(true);
		rueckgabeRepository.save(rueckgabe);
		ProPaySchnittstelle.post("reservation/release/" + rueckgabe.getAusleihender() + "?reservationId=" + rueckgabe.getProPayID());
		model.addAttribute("link", "account/" + benutzername + "/zurueckgegebeneartikel");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachrichten")
	@PreAuthorize("#benutzername == authentication.name")
	public String nachrichtenUebersicht(Model model, @PathVariable String benutzername) {
		if (benutzerRepository.findByBenutzername(benutzername).get().getRolle().equals("ROLE_ADMIN")) {
			model.addAttribute("admin", true);
		}
		model.addAttribute("messages", messageRepository.findByEmpfaenger(benutzername));
		return "nachrichtenUebersicht";
	}

	@GetMapping("/account/{benutzername}/konflikt/send/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String konfliktErstellen(Model model, @PathVariable String benutzername) {
		Konflikt konflikt = new Konflikt();
		model.addAttribute("konflikt", konflikt);
		return "konfliktErstellung";
	}

	@PostMapping("/account/{benutzername}/konflikt/send/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String konfliktAbsenden(Model model, @PathVariable String benutzername, @PathVariable Long id, Konflikt konflikt, Principal principal) {
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		konflikt.setAbsenderMail(benutzerRepository.findByBenutzername(benutzername).get().getEmail());
		konflikt.setVerursacherMail(benutzerRepository.findByBenutzername(rueckgabe.getAusleihender()).get().getEmail());
		konflikt.setRueckgabe(rueckgabe);
		konfliktRepository.save(konflikt);
		Message message = new Message(principal.getName(), rueckgabe.getAusleihender(), "Der Artikel " + rueckgabe.getArtikel().getArtikelName() + " wurde im mangelhaften Zustand zurückgegeben. Der Fall wurde an die Konfliktlösestelle übergeben.");
		messageRepository.save(message);
		message = new Message("System", principal.getName(), "Ein Konflikt wurde mit folgender Beschreibung an die Konfliktlösestelle übergeben: " + konflikt.getBeschreibung());
		messageRepository.save(message);
		//iMailService.sendEmailToKonfliktLoeseStelle(benutzername,konflikt.getBeschreibung(),id);
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachricht/delete/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String messageDelete(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		messageRepository.delete(messageRepository.findById(id).get());
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
	}
	
	@GetMapping("/account/{benutzername}/transaktionUebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String transaktionen(Model model, @PathVariable String benutzername, Principal principal) {
		for (Rueckgabe rueckgabe : rueckgabeRepository.findAll()) {
			if (rueckgabe.isAngenommen()) {
				model.addAttribute("artikel", rueckgabeRepository.findByVerleiherName(principal.getName()));
				model.addAttribute("artikelAusgeliehen", rueckgabeRepository.findByAusleihender(principal.getName()));
			}
		}
		return "transaktionenUebersicht";
	}

	@GetMapping("/admin/konflikte")
	public String konfliktUebersicht(Model model, Principal principal) {
		model.addAttribute("offeneKonflikte", konfliktRepository.findAllByInBearbeitung("offen"));
		model.addAttribute("meineKonflikte", konfliktRepository.findAllByBearbeitender(principal.getName()));
		return "konfliktAnsicht";
	}

	@GetMapping("/admin/konflikte/{id}")
	public String konfliktUebernehmen(Model model, @PathVariable Long id, Principal principal) {
		Konflikt konflikt = konfliktRepository.findById(id).get();
		if ("offen".equals(konflikt.getInBearbeitung())) {
			konflikt.setInBearbeitung("inBearbeitung");
			konflikt.setBearbeitender(principal.getName());
			konfliktRepository.save(konflikt);
		}
		model.addAttribute("konflikt", konflikt);
		return "konfliktDetail";
	}

	@PostMapping("/admin/konflikte/{id}")
	public String konfliktLoesen(Model model, @PathVariable Long id, String benutzer) {
		Konflikt konflikt = konfliktRepository.findById(id).get();
		konflikt.setInBearbeitung("geschlossen");
		konfliktRepository.save(konflikt);
		if ("Verleihender".equals(benutzer)) {
			Message message = new Message("Admin", konflikt.getRueckgabe().getVerleiherName(), "Sie erhalten die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName() + " zurück");
			messageRepository.save(message);
			message = new Message("Admin", konflikt.getRueckgabe().getAusleihender(), "Der Verleiher erhält die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName() + " zurück");
			messageRepository.save(message);
			ProPaySchnittstelle.post("reservation/punish/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		} else {
			Message message = new Message("Admin", konflikt.getRueckgabe().getAusleihender(), "Sie erhalten die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName() + " zurück");
			messageRepository.save(message);
			message = new Message("Admin", konflikt.getRueckgabe().getVerleiherName(), "Der Ausleihende erhält die Kaution für " + konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName() + " zurück");
			messageRepository.save(message);
			ProPaySchnittstelle.post("reservation/release/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		}
		model.addAttribute("link", "admin/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/zugriffVerweigert")
	public String zugriffVerweigert(){
		return "zugriffVerweigert";
	}
}