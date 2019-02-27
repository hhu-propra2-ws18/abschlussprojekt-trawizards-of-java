package de.trawizardsOfJava.messenger.web;

import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.messenger.data.NachrichtenRepo;
import de.trawizardsOfJava.messenger.data.SessionRepo;
import de.trawizardsOfJava.messenger.model.Nachricht;
import de.trawizardsOfJava.messenger.model.Session;
import de.trawizardsOfJava.messenger.model.Teilnehmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

import static java.time.LocalDateTime.now;

@Controller
public class ChatController {

	@Autowired
	NachrichtenRepo nachrichtenRepo;

	@Autowired
	SessionRepo sessionRepo;

	@Autowired
	BenutzerRepository benutzerRepository;

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/messenger/{benutzername}/{empfaenger}/start")
	@PreAuthorize("#benutzername == authentication.name")
	public String uebersicht(Model model, @PathVariable String benutzername, @PathVariable String empfaenger) {
		String benutzerName = benutzername.toLowerCase();
		String empfaengerName = empfaenger.toLowerCase();
		Teilnehmer teilnehmer = new Teilnehmer(benutzerName, empfaengerName);
		Session session = new Session(teilnehmer);
		Long sessionId = session.isExisting(sessionRepo.findAll());
		if(sessionId == -1) {
			sessionRepo.save(session);
			sessionId = sessionRepo.findByTeilnehmer(teilnehmer).getId();
		}
		model.addAttribute("link", sessionId);
		return "Chat/start";
	}

	@GetMapping("/messenger/{benutzername}/{sessionId}")
	@PreAuthorize("#benutzername == authentication.name")
	public String sessionChat(Model model, @PathVariable Long sessionId, @PathVariable String benutzername) {
		Session session = sessionRepo.findById(sessionId).get();
		Nachricht nachricht = new Nachricht(benutzerRepository.findByBenutzername(benutzername).get(), session);
		model.addAttribute("nachricht", nachricht);
		model.addAttribute("teilnehmer", sessionId);
		model.addAttribute("hide", false);
		model.addAttribute("sessions", sessionRepo.findByTeilnehmer_PersonEinsOrTeilnehmer_PersonZwei(benutzername, benutzername));
		return "Chat/chat";
	}

	@GetMapping("/messenger/{benutzername}")
	@PreAuthorize("#benutzername == authentication.name")
	public String allChats(Model model, @PathVariable String benutzername) {
		model.addAttribute("sessions", sessionRepo.findByTeilnehmer_PersonEinsOrTeilnehmer_PersonZwei(benutzername, benutzername));
		model.addAttribute("hide", true);
		model.addAttribute("sessions", sessionRepo.findByTeilnehmer_PersonEinsOrTeilnehmer_PersonZwei(benutzername, benutzername));
		return "Chat/chat";
	}

	@GetMapping("/messenger/{benutzername}/{sessionId}/reload")
	@PreAuthorize("#benutzername == authentication.name")
	public String reloadChat(Model model, @PathVariable Long sessionId, @PathVariable String benutzername) {
		Session session = sessionRepo.findById(sessionId).get();
		model.addAttribute("nachrichten", nachrichtenRepo.findBySession(session));
		return "Chat/reloadChat";
	}

	@PostMapping("/messenger/{benutzername}/{sessionId}")
	public String sendMessage(Model model, @PathVariable Long sessionId, Nachricht nachricht, @PathVariable String benutzername){
		nachricht.setGesendet(now());
		nachrichtenRepo.save(nachricht);

		model.addAttribute("chat", nachrichtenRepo.findBySession(sessionRepo.findById(sessionId).get()));
		return sessionChat(model, sessionId, benutzername);
	}
}
