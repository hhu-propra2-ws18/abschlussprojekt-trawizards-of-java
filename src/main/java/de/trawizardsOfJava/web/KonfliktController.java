package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Konflikt;
import de.trawizardsOfJava.model.Rueckgabe;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class KonfliktController {

	private BenutzerRepository benutzerRepository;
	private RueckgabeRepository rueckgabeRepository;
	private KonfliktRepository konfliktRepository;
	private MessageRepository messageRepository;
	private IProPaySchnittstelle proPaySchnittstelle;
	//private IMailService iMailService;

	@Autowired
	public KonfliktController(BenutzerRepository benutzerRepository, RueckgabeRepository rueckgabeRepository,
							  KonfliktRepository konfliktRepository, MessageRepository messageRepository,
							  IProPaySchnittstelle proPaySchnittstelle/*, IMailService iMailService*/) {
		this.benutzerRepository = benutzerRepository;
		this.rueckgabeRepository = rueckgabeRepository;
		this.konfliktRepository = konfliktRepository;
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

	@GetMapping("/account/{benutzername}/konflikt/send/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String konfliktErstellen(Model model, @PathVariable String benutzername) {
		model.addAttribute("konflikt", new Konflikt());
		return "konfliktErstellung";
	}

	@PostMapping("/account/{benutzername}/konflikt/send/{id}")
	@PreAuthorize("#benutzername == authentication.name")
	public String konfliktAbsenden(Model model, @PathVariable String benutzername, @PathVariable Long id, Konflikt konflikt) {
		Rueckgabe rueckgabe = rueckgabeRepository.findById(id).get();
		konflikt.setKonflikt(rueckgabe, benutzerRepository.findByBenutzername(rueckgabe.getAusleihender()).get().getEmail(), benutzerRepository.findByBenutzername(benutzername).get().getEmail());
		konfliktRepository.save(konflikt);
		messageRepository.save(new Message(rueckgabe, "abgelehnt"));
		//iMailService.sendEmailToKonfliktLoeseStelle(benutzername,konflikt.getBeschreibung(),id);
		model.addAttribute("link", "account/" + benutzername + "/nachrichten");
		return "backToTheFuture";
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
	public String konfliktLoesen(Model model, @PathVariable Long id, String benutzer, Principal principal) {
		if (proPaySchnittstelle.getEntity(benutzer).getAmount() == null){
			model.addAttribute("proPayError", true);
			return konfliktUebernehmen(model, id, principal);
		}
		Konflikt konflikt = konfliktRepository.findById(id).get();
		konflikt.setInBearbeitung("geschlossen");
		konfliktRepository.save(konflikt);
		Message[] messages;
		if ("Verleihender".equals(benutzer)) {
			messages = Message.konfliktMessages(konflikt, "Verleihenden");
			proPaySchnittstelle.post("reservation/punish/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		} else {
			messages = Message.konfliktMessages(konflikt, "Ausleihenden");
			proPaySchnittstelle.post("reservation/release/" + konflikt.getRueckgabe().getAusleihender() + "?reservationId=" + konflikt.getRueckgabe().getProPayID());
		}
		messageRepository.save(messages[0]);
		messageRepository.save(messages[1]);
		model.addAttribute("link", "admin/konflikte");
		return "backToTheFuture";
	}
}
