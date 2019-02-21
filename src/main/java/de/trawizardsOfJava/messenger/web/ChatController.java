package de.trawizardsOfJava.messenger.web;

import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.messenger.data.NachrichtenRepo;
import de.trawizardsOfJava.messenger.data.SessionRepo;
import de.trawizardsOfJava.messenger.model.Nachricht;
import de.trawizardsOfJava.messenger.model.Session;
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

	@GetMapping("/messenger/{benutzername}")
	@PreAuthorize("#benutzername == authentication.name")
	public String uebersicht(Model model, @PathVariable String benutzername) {
		ArrayList<Session> sessions = sessionRepo.findAll();
		model.addAttribute("teilnehmer", sessions.get(0).getId());//sessionRepo.findById(teilnehmer).getId());
		return "start";
	}

	@GetMapping("/messenger/{benutzername}/{sessionId}")
	@PreAuthorize("#benutzername == authentication.name")
	public String sessionChat(Model model, @PathVariable("sessionId") Long sessionId, @PathVariable("benutzername") String benutzername) {
		Session session = sessionRepo.findById(sessionId).get();
		System.out.println(nachrichtenRepo.findBySession(session));
		model.addAttribute("nachrichten", nachrichtenRepo.findBySession(session));
		Nachricht nachricht = new Nachricht();
		nachricht.setSession(session);
		nachricht.setAbsender(benutzerRepository.findByBenutzername(benutzername).get());
		model.addAttribute("nachricht", nachricht);
		model.addAttribute("name", benutzername);
		model.addAttribute("teilnehmer", sessionId);
		return "chat";
	}

	@GetMapping("/messenger/{benutzername}/{sessionId}/reload")
	@PreAuthorize("#benutzername == authentication.name")
	public String reloadChat(Model model, @PathVariable("sessionId") Long sessionId, @PathVariable("benutzername") String benutzername) {
		Session session = sessionRepo.findById(sessionId).get();
		System.out.println(nachrichtenRepo.findBySession(session));
		model.addAttribute("nachrichten", nachrichtenRepo.findBySession(session));
		Nachricht nachricht = new Nachricht();
		nachricht.setSession(session);
		nachricht.setAbsender(benutzerRepository.findByBenutzername(benutzername).get());
		model.addAttribute("nachricht", nachricht);
		model.addAttribute("name", benutzername);
		model.addAttribute("teilnehmer", sessionId);
		return "reloadChat";
	}

	@PostMapping("/messenger/{benutzername}/{sessionId}")
	public String sendMessage(Model model, @PathVariable("sessionId") Long sessionId, Nachricht nachricht, @PathVariable("benutzername") String benutzername){
		nachricht.setGesendet(now());
		nachrichtenRepo.save(nachricht);
		model.addAttribute("chat", nachrichtenRepo.findBySession(sessionRepo.findById(sessionId).get()));
		return sessionChat(model, sessionId, benutzername);
	}
}
