package de.trawizardsofjava.messenger.web;

import de.trawizardsofjava.messenger.data.NachrichtenRepo;
import de.trawizardsofjava.messenger.data.SessionRepo;
import de.trawizardsofjava.messenger.model.Nachricht;
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

import static java.time.LocalDateTime.now;

@Controller
public class ChatController {

	@Autowired
	NachrichtenRepo nachrichtenRepo;

	@Autowired
	SessionRepo sessionRepo;

	@GetMapping("/")
	public String uebersicht(Model model) {
		//model.addAttribute("teilnehmer", );
		return "chat";
	}

	@GetMapping("/{id}")
	public String sessionChat(Model model, @PathVariable Teilnehmer teilnehmer) {
		model.addAttribute("chat", nachrichtenRepo.findBySession(sessionRepo.findByTeilnehmer(teilnehmer)));
		model.addAttribute("teilnehmer", teilnehmer);
		return "chat";
	}

	@PostMapping("/{id}")
	public String sendMessage(Model model, @PathVariable Teilnehmer id, Nachricht nachricht){
		nachricht.setGesendet(now());
		nachrichtenRepo.save(nachricht);
		model.addAttribute("chat", nachrichtenRepo.findBySession(sessionRepo.findByTeilnehmer(id)));
		return sessionChat(model, id);
	}

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public Nachricht sendMessage(@Payload Nachricht nachricht) {
		return nachricht;
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public Nachricht addUser(@Payload Nachricht nachricht,
							   SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		headerAccessor.getSessionAttributes().put("username", nachricht.getAbsender());
		return nachricht;
	}
}
