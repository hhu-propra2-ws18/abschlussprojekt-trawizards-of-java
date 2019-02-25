package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.*;
import de.trawizardsOfJava.model.*;
import de.trawizardsOfJava.proPay.*;
import de.trawizardsOfJava.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AppController {
	private BenutzerRepository benutzerRepository;
	private ArtikelRepository artikelRepository;
	private AusleiheRepository ausleiheRepository;
	private MessageRepository messageRepository;
	private RueckgabeRepository rueckgabeRepository;
	private KonfliktRepository konfliktRepository;
	//private IMailService iMailService;
	private static final String ALTERNATIVE_PHOTO = "kein-bild-vorhanden.jpg";

	@Autowired
	public AppController(BenutzerRepository benutzerRepository, ArtikelRepository artikelRepository,
						 AusleiheRepository ausleiheRepository, MessageRepository messageRepository,
						 RueckgabeRepository rueckgabeRepository, KonfliktRepository konfliktRepository/*,
						 IMailService iMailService*/) {
		this.benutzerRepository = benutzerRepository;
		this.artikelRepository = artikelRepository;
		this.ausleiheRepository = ausleiheRepository;
		this.messageRepository = messageRepository;
		this.rueckgabeRepository = rueckgabeRepository;
		this.konfliktRepository = konfliktRepository;
		//this.iMailService = iMailService;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	/*
		Diese Methode greift auf das Dateisystem des Dockercontainers zu und liefert das angefragte Bild aus.

	*/
	@ResponseBody
	@RequestMapping(value = "/detail/{id}", method = GET, produces = MediaType.IMAGE_JPEG_VALUE)
	@PreAuthorize("#benutzername == authentication.name")
	public Resource getImageAsResource(@PathVariable("id") Long id) {
		String test = artikelRepository.findById(id).get().getFotos().get(0);
		System.out.println("testa" + new FileSystemResource("fotos/" + test + ".jpg"));
		return new FileSystemResource("fotos/" + test + ".jpg");
	}


	@GetMapping("/")
	public String startseite(Model model, Principal principal) {
		model.addAttribute("artikel", artikelRepository.findAll());
		model.addAttribute("aktuelleSeite", "Startseite");
		model.addAttribute("angemeldet", principal != null);
		return "startseite";
	}

	@GetMapping("/search")
	public String suche(Model model, @RequestParam final String q, Principal principal) {
		model.addAttribute("artikel", artikelRepository.findAllByArtikelNameContaining(q));
		model.addAttribute("query", q);
		model.addAttribute("aktuelleSeite", "Suche");
		model.addAttribute("angemeldet", principal != null);
		return "suche";
	}

	//public static String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/fotos/";

	@GetMapping("/detail/{id}")
	public String artikelDetail(Model model, @PathVariable Long id, Principal principal) {
		model.addAttribute("artikelDetail", artikelRepository.findById(id).get());
		model.addAttribute("aktuelleSeite", "Artikelansicht");
		model.addAttribute("angemeldet", principal != null);


		if(artikelRepository.findById(id).get().getFotos().get(0).equals("fotos")){
			model.addAttribute("fotoTest", ALTERNATIVE_PHOTO);
			System.out.println("in ALT fotos");
		}else{
			model.addAttribute("fotoTest", artikelRepository.findById(id).get().getFotos().get(0));
			System.out.println("fotoTest " + artikelRepository.findById(id).get().getFotos().get(0));
			System.out.println("in right fotots");
		}


		return "artikelDetail";
	}

	@GetMapping("/account/{benutzername}/aendereArtikel/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String artikelAendern(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		model.addAttribute("artikel", artikelRepository.findById(id).get());
		return "artikelAendern";
	}

	@PostMapping("/account/{benutzername}/aendereArtikel/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherArtikelAenderung(Model model, @PathVariable String benutzername, Artikel artikel, String daterange) {
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
	public String speicherPerson(Model model, Person person) {
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
	public String profilAnsicht(Model model, @PathVariable String benutzername, Principal principal) {
		model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(benutzername));
		model.addAttribute("isUser", benutzername.equals(principal.getName()));
		model.addAttribute("proPay", ProPaySchnittstelle.getEntity(benutzername));
		model.addAttribute("angemeldet", true);
		model.addAttribute("aktuelleSeite", "Profil");

		//Überprüfung, ob eine Rückgabe fällig ist
		for (Ausleihe ausleihe : ausleiheRepository.findByAusleihender(benutzername)) {
			if (ausleihe.faelligeAusleihe()) {
				//iMailService.sendReminder(person.getEmail(),person.getName(), ausleihe.getArtikel().getArtikelName());
				model.addAttribute("message", "true");
				model.addAttribute("artikelName", ausleihe.getArtikel().getArtikelName());
				model.addAttribute("verleiherName", ausleihe.getVerleiherName());
			}
		}
		return "profilAnsicht";
	}

	@PostMapping("/account/{benutzername}")
	@PreAuthorize("#benutzername == authentication.name")
	public String kontoAufladen(Model model, @PathVariable String benutzername, Long amount, Principal principal) {
		ProPaySchnittstelle.post("account/" + benutzername + "?amount=" + amount);
		return profilAnsicht(model, benutzername, principal);
	}

	@GetMapping("/account/{benutzername}/bearbeitung")
	@PreAuthorize("#benutzername == authentication.name")
	public String profilAendern(Model model, @PathVariable String benutzername) {
		model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
		return "profilAendern";
	}

	@PostMapping("/account/{benutzername}/bearbeitung")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherProfilAenderung(Model model, @PathVariable String benutzername, Person person) {
		benutzerRepository.save((person));
		model.addAttribute("link", "account/" + person.getBenutzername());
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/erstelleArtikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String erstelleArtikel(Model model, @PathVariable String benutzername) {
		model.addAttribute("artikel", new Artikel());
		return "artikelErstellung";
	}

	@PostMapping("/account/{benutzername}/erstelleArtikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherArtikel(Model model, @PathVariable String benutzername, String daterange, Artikel artikel) {
		artikel.setVerfuegbarkeit(new Verfuegbarkeit(daterange));
		artikel.setVerleiherBenutzername(benutzername);
		artikel.setFotos(new ArrayList<String>());
		artikelRepository.save(artikel);
		model.addAttribute("link", "account/" + benutzername);

		return "redirect:/fotoupload/"+artikel.getId();
	}

	@GetMapping("/account/{benutzername}/artikel/{id}/anfrage")
	@PreAuthorize("#benutzername == authentication.name")
	public String neueAnfrage(Model model, @PathVariable String benutzername, @PathVariable Long id) {
		Artikel artikel = artikelRepository.findById(id).get();
		ArrayList<Verfuegbarkeit> verfuegbarkeiten = new ArrayList<>();
		for (Ausleihe ausleihe : ausleiheRepository.findByArtikel(artikel)) {
			verfuegbarkeiten.add(ausleihe.getVerfuegbarkeit());
		}
		model.addAttribute("daten", verfuegbarkeiten);
		model.addAttribute("verfuegbar", artikel.getVerfuegbarkeit());
		return "ausleihe";
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
		Message message = new Message(ausleihe, "angefragt");
		messageRepository.save(message);
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
			Message message = new Message(ausleihe, "angenommen");
			messageRepository.save(message);
		} else {
			ausleiheRepository.delete(ausleihe);
			Message message = new Message(ausleihe, "abgelehnt");
			messageRepository.save(message);
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
		Message message = new Message(rueckgabe, "angefragt");
		messageRepository.save(message);
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
		Message message = new Message(rueckgabe, "angenommen");
		messageRepository.save(message);
		ProPaySchnittstelle.post("reservation/release/" + rueckgabe.getAusleihender() + "?reservationId=" + rueckgabe.getProPayID());
		return rueckgabenUebersicht(model, benutzername, principal);
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
		Message message = new Message(rueckgabe, "abgelehnt");
		messageRepository.save(message);
		//iMailService.sendEmailToKonfliktLoeseStelle(benutzername,konflikt.getBeschreibung(),id);
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/nachrichten")
	@PreAuthorize("#benutzername == authentication.name")
	public String nachrichtenUebersicht(Model model, @PathVariable String benutzername) {
		model.addAttribute("admin", benutzerRepository.findByBenutzername(benutzername).get().getRolle().equals("ROLE_ADMIN"));
		model.addAttribute("messages", messageRepository.findByEmpfaengerOrAbsender(benutzername, benutzername));
		return "nachrichtenUebersicht";
	}

	@PostMapping("/account/{benutzername}/nachrichten")
	@PreAuthorize("#benutzername == authentication.name")
	public String loescheNachricht(Model model, @PathVariable String benutzername, Long id) {
		messageRepository.delete(messageRepository.findById(id).get());
		return nachrichtenUebersicht(model, benutzername);
	}

	@GetMapping("/account/{benutzername}/transaktionUebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String transaktionen(Model model, @PathVariable String benutzername) {
		for (Rueckgabe rueckgabe : rueckgabeRepository.findAll()) {
			if (rueckgabe.isAngenommen()) {
				model.addAttribute("artikel", rueckgabeRepository.findByVerleiherName(benutzername));
				model.addAttribute("artikelAusgeliehen", rueckgabeRepository.findByAusleihender(benutzername));
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
		konflikt.nehmeKonfliktAn(principal.getName());
		konfliktRepository.save(konflikt);
		model.addAttribute("konflikt", konflikt);
		return "konfliktDetail";
	}

	@PostMapping("/admin/konflikte/{id}")
	public String konfliktLoesen(Model model, @PathVariable Long id, String benutzer) {
		Konflikt konflikt = konfliktRepository.findById(id).get();
		konflikt.setInBearbeitung("geschlossen");
		konfliktRepository.save(konflikt);
		Message[] messages;
		if ("Verleihender".equals(benutzer)) {
			messages = Message.konfliktMessages(konflikt, "Verleihenden");
			ProPaySchnittstelle.post("reservation/punish/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		} else {
			messages = Message.konfliktMessages(konflikt, "Ausleihenden");
			ProPaySchnittstelle.post("reservation/release/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		}
		messageRepository.save(messages[0]);
		messageRepository.save(messages[1]);
		model.addAttribute("link", "admin/konflikte");
		return "backToTheFuture";
	}

	@GetMapping("/zugriffVerweigert")
	public String zugriffVerweigert() {
		return "zugriffVerweigert";
	}
}