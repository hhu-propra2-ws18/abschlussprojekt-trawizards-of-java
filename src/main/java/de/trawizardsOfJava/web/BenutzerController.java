package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Ausleihe;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import de.trawizardsOfJava.proPay.ProPay;
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
public class BenutzerController {

	private BenutzerRepository benutzerRepository;
	private ArtikelRepository artikelRepository;
	private AusleiheRepository ausleiheRepository;
	private RueckgabeRepository rueckgabeRepository;
	private MessageRepository messageRepository;
	private IProPaySchnittstelle proPaySchnittstelle;
	//private IMailService iMailService;

	@Autowired
	public BenutzerController(BenutzerRepository benutzerRepository, ArtikelRepository artikelRepository,
							  AusleiheRepository ausleiheRepository, RueckgabeRepository rueckgabeRepository,
							  MessageRepository messageRepository, IProPaySchnittstelle proPaySchnittstelle/*,
							  IMailService iMailService*/) {
		this.benutzerRepository = benutzerRepository;
		this.artikelRepository = artikelRepository;
		this.ausleiheRepository = ausleiheRepository;
		this.rueckgabeRepository = rueckgabeRepository;
		this.messageRepository = messageRepository;
		this.proPaySchnittstelle = proPaySchnittstelle;
		//this.iMailService = iMailService;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/account/{benutzername}")
	public String profilAnsicht(Model model, @PathVariable String benutzername, Principal principal) {
		model.addAttribute("person", benutzerRepository.findByBenutzername(benutzername).get());
		model.addAttribute("artikel", artikelRepository.findByVerleiherBenutzername(benutzername));
		model.addAttribute("isUser", benutzername.equals(principal.getName()));
		ProPay proPay = proPaySchnittstelle.getEntity(benutzername);
		model.addAttribute("proPayError", proPay.getAmount() == null);
		model.addAttribute("proPay", proPay);
		model.addAttribute("angemeldet", true);
		model.addAttribute("aktuelleSeite", "Profil");
		findeFaelligeAusleihe(model, ausleiheRepository.findByAusleihender(benutzername));
		neueNachricht(model, messageRepository.findByEmpfaenger(benutzername));
		return "profilAnsicht";
	}

	private void findeFaelligeAusleihe(Model model, ArrayList<Ausleihe> ausleihen) {
		for (Ausleihe ausleihe : ausleihen) {
			if (ausleihe.faelligeAusleihe()) {
				//Person person = benutzerRepository.findByBenutzername(ausleihe.getAusleihender()).get();
				//iMailService.sendReminder(person.getEmail(),person.getName(), ausleihe.getArtikel().getArtikelName());
				model.addAttribute("message", "true");
				model.addAttribute("artikelName", ausleihe.getArtikel().getArtikelName());
				model.addAttribute("verleiherName", ausleihe.getVerleiherName());
			}
		}
	}


	private void neueNachricht(Model model, ArrayList<Message> nachrichten) {
		for (Message message : nachrichten) {
				model.addAttribute("nachricht", "true");
		}

	}

	@PostMapping("/account/{benutzername}")
	@PreAuthorize("#benutzername == authentication.name")
	public String kontoAufladen(Model model, @PathVariable String benutzername, Long amount, Principal principal) {
		proPaySchnittstelle.post("account/" + benutzername + "?amount=" + amount);
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

	@GetMapping("/account/{benutzername}/transaktionUebersicht")
	@PreAuthorize("#benutzername == authentication.name")
	public String transaktionen(Model model, @PathVariable String benutzername) {
		model.addAttribute("artikel", rueckgabeRepository.findByVerleiherName(benutzername));
		model.addAttribute("artikelAusgeliehen", rueckgabeRepository.findByAusleihender(benutzername));
		return "transaktionenUebersicht";
	}

	@GetMapping("/account/{benutzername}/nachrichten")
	@PreAuthorize("#benutzername == authentication.name")
	public String nachrichtenUebersicht(Model model, @PathVariable String benutzername) {
		model.addAttribute("admin", benutzerRepository.findByBenutzername(benutzername).get().getRolle().equals("ROLE_ADMIN"));
		model.addAttribute("messages", messageRepository.findByEmpfaenger(benutzername));
		return "nachrichtenUebersicht";
	}

	@PostMapping("/account/{benutzername}/nachrichten")
	@PreAuthorize("#benutzername == authentication.name")
	public String loescheNachricht(Model model, @PathVariable String benutzername, Long id) {
		messageRepository.delete(messageRepository.findById(id).get());
		return nachrichtenUebersicht(model, benutzername);
	}
}
