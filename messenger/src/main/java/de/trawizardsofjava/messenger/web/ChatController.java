package de.trawizardsofjava.messenger.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {
	@GetMapping("/")
	public String uebersicht(Model model) {
		return "Chat";
	}

	@GetMapping("/{id}")
	public String sessionChat(Model model, @PathVariable Long sessionId) {
		return "Chat";
	}

}
