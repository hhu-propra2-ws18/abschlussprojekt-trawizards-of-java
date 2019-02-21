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
		model.addAttribute("artikel", artikelRepository.findAll());
		model.addAttribute("aktuelleSeite", "Startseite");
		model.addAttribute("angemeldet", principal != null);
		return "uebersichtSeite";
	}

	@GetMapping("/search")
	public String search(Model model, @RequestParam final String q, Principal principal) {
		model.addAttribute("artikel", artikelRepository.findAllByArtikelNameContaining(q));
		model.addAttribute("query", q);
		model.addAttribute("aktuelleSeite", "Suche");
		model.addAttribute("angemeldet", principal != null);
		return "search";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		model.addAttribute("artikelDetail", artikelRepository.findById(id).get());
		model.addAttribute("aktuelleSeite", "Artikelansicht");
		model.addAttribute("angemeldet", principal != null);
		return "artikelDetail";
	}

	@GetMapping("/account/{benutzername}/aendereArtikel/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String changeItem(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		model.addAttribute("artikel", artikelRepository.findById(id).get());
		return "changeItem";
	}

	@PostMapping("/account/{benutzername}/aendereArtikel/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String postChangeItem(Model model, @PathVariable String benutzername, Artikel artikel, String daterange) {
		artikel.setVerfuegbarkeit(new Verfuegbarkeit(daterange));
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
		model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(benutzername));
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
		model.addAttribute("artikel", new Artikel());
		return "addItem";
	}

	@PostMapping("/account/{benutzername}/addItem")
	@PreAuthorize("#benutzername == authentication.name")
	public String postAddItem(Model model, @PathVariable String benutzername, String daterange, Artikel artikel) {
		artikel.setVerfuegbarkeit(new Verfuegbarkeit(daterange));
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
		Ausleihe ausleihe = new Ausleihe(artikel, new Verfuegbarkeit(daterange), principal.getName());
		if (! ProPaySchnittstelle.getEntity(principal.getName()).genuegendGeld(ausleihe.berechneGesamtPreis(), ausleiheRepository.findByAusleihenderAndAccepted(principal.getName(), false))) {
			model.addAttribute("error", true);
			return neueAnfrage(model, benutzername, id);
		}
		ausleiheRepository.save(ausleihe);
		Message message = new Message(principal.getName(), artikel.getVerleiherBenutzername(), Message.generiereNachricht("AnfrageGestellt", principal.getName(), artikel.getArtikelName()));
		messageRepository.save(message);
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/ausleihenuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String ausleihenuebersicht(Model model, @PathVariable String benutzername) {
		model.addAttribute("ausleihen", ausleiheRepository.findByVerleiherName(benutzername));
		return "ausleihenuebersicht";
	}

	@GetMapping("/account/{benutzername}/annahme/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String ausleihebestaetigt(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		ausleihe.setAccepted(true);
		ausleiheRepository.save(ausleihe);
		Message message = new Message(principal.getName(), ausleihe.getAusleihender(), Message.generiereNachricht("AnfrageAngenommen", principal.getName(), ausleihe.getArtikel().getArtikelName()));
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
		Message message = new Message(principal.getName(), ausleihe.getAusleihender(), Message.generiereNachricht("AnfrageAbgelehnt", principal.getName(), ausleihe.getArtikel().getArtikelName()));
		messageRepository.save(message);
		model.addAttribute("link", "account/" + benutzername + "/ausleihenuebersicht");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/ausgelieheneuebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String leihenuebersicht(Model model, @PathVariable String benutzername, Principal principal) {
		model.addAttribute("ausleihen", ausleiheRepository.findByAusleihender(principal.getName()));
		return "ausgelieheneuebersicht";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String zurueckgegeben(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Ausleihe ausleihe = ausleiheRepository.findById(id).get();
		rueckgabeRepository.save(new Rueckgabe(ausleihe));
		Message message = new Message(principal.getName(), ausleihe.getVerleiherName(), Message.generiereNachricht("Rueckgabe", principal.getName(), ausleihe.getArtikel().getArtikelName()));
		messageRepository.save(message);
		ausleiheRepository.delete(ausleiheRepository.findById(id).get());
		model.addAttribute("link", "account/" + benutzername + "/ausgelieheneuebersicht");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/zurueckgegebeneartikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String rueckgabenuebersicht(Model model, @PathVariable String benutzername, Principal principal) {
		model.addAttribute("ausleihen", rueckgabeRepository.findByVerleiherName(principal.getName()));
		return "zurueckgegebeneartikel";
	}

	@GetMapping("/account/{benutzername}/rueckgabe/akzeptiert/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String rueckgabeakzeptiert(Model model, @PathVariable String benutzername, @PathVariable Long id, Principal principal) {
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		Message message = new Message(principal.getName(), rueckgabe.getAusleihender(), Message.generiereNachricht("RueckgabeAkzeptiert", principal.getName(), rueckgabe.getArtikel().getArtikelName()));
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
		model.addAttribute("admin", benutzerRepository.findByBenutzername(benutzername).get().getRolle().equals("ROLE_ADMIN"));
		model.addAttribute("messages", messageRepository.findByEmpfaengerOrAbsender(benutzername, benutzername));
		return "nachrichtenUebersicht";
	}

	@GetMapping("/account/{benutzername}/konflikt/send/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String konfliktErstellen(Model model, @PathVariable String benutzername) {
		model.addAttribute("konflikt", new Konflikt());
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
		Message message = new Message(principal.getName(), rueckgabe.getAusleihender(), Message.generiereNachricht("RueckgabeAbgelehnt", principal.getName(), rueckgabe.getArtikel().getArtikelName()));
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
			Message message = new Message("Admin", konflikt.getRueckgabe().getVerleiherName(), Message.generiereNachricht("Konflikt", konflikt.getRueckgabe().getVerleiherName(), konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName()));
			messageRepository.save(message);
			message = new Message("Admin", konflikt.getRueckgabe().getAusleihender(), Message.generiereNachricht("Konflikt", konflikt.getRueckgabe().getVerleiherName(), konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName()));
			messageRepository.save(message);
			ProPaySchnittstelle.post("reservation/punish/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		} else {
			Message message = new Message("Admin", konflikt.getRueckgabe().getAusleihender(), Message.generiereNachricht("Konflikt", konflikt.getRueckgabe().getAusleihender(), konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName()));
			messageRepository.save(message);
			message = new Message("Admin", konflikt.getRueckgabe().getVerleiherName(), Message.generiereNachricht("Konflikt", konflikt.getRueckgabe().getAusleihender(), konfliktRepository.findById(id).get().getRueckgabe().getArtikel().getArtikelName()));
			messageRepository.save(message);
			ProPaySchnittstelle.post("reservation/release/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		}
		model.addAttribute("link", "admin/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/zugriffVerweigert")
	public String zugriffVerweigert() {
		return "zugriffVerweigert";
	}
}