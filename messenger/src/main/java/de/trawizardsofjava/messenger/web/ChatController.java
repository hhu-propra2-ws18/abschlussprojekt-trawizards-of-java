package de.trawizardsofjava.messenger.web;

import de.trawizardsofjava.messenger.data.DataIni;
import de.trawizardsofjava.messenger.data.NachrichtenRepo;
import de.trawizardsofjava.messenger.data.PersonRepo;
import de.trawizardsofjava.messenger.data.SessionRepo;
import de.trawizardsofjava.messenger.model.Nachricht;
import de.trawizardsofjava.messenger.model.Session;
import de.trawizardsofjava.messenger.model.Teilnehmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Controller
public class ChatController {

	@Autowired
	NachrichtenRepo nachrichtenRepo;

	@Autowired
	SessionRepo sessionRepo;

	@Autowired
	PersonRepo personRepo;

	@GetMapping("/")
	public String uebersicht(Model model) {
		model.addAttribute("teilnehmer", sessionRepo.findAll().get(0).getId());
		return "start";
	}

	@GetMapping("/{sessionId}")
	public String sessionChat(Model model, @PathVariable Long sessionId) {
		Session session = sessionRepo.findById(sessionId).get();
		System.out.println(nachrichtenRepo.findBySession(session));
		model.addAttribute("nachrichten", nachrichtenRepo.findBySession(session));
		Nachricht nachricht = new Nachricht();
		nachricht.setSession(session);
		nachricht.setAbsender(personRepo.findByPersonName("Ocramir"));
		model.addAttribute("nachricht", nachricht);
		model.addAttribute("name", "Ocramir");
		model.addAttribute("teilnehmer", sessionId);
		return "chat";
	}

	@PostMapping("/{sessionId}")
	public String sendMessage(Model model, @PathVariable Long sessionId, Nachricht nachricht){
		nachricht.setGesendet(now());
		nachrichtenRepo.save(nachricht);
		model.addAttribute("chat", nachrichtenRepo.findBySession(sessionRepo.findById(sessionId).get()));
		return sessionChat(model, sessionId);
	}
}
