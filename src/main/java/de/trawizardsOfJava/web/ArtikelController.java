package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Verfuegbarkeit;
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
public class ArtikelController {
	private ArtikelRepository artikelRepository;
	private MessageRepository messageRepository;

	@Autowired
	public ArtikelController(ArtikelRepository artikelRepository, MessageRepository messageRepository) {
		this.artikelRepository = artikelRepository;
		this.messageRepository = messageRepository;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/account/{benutzername}/erstelleArtikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String erstelleArtikel(Model model, @PathVariable String benutzername) {
		model.addAttribute("artikel", new Artikel());
		messageRepository.save(new Message(benutzername, "eingestellt"));
		return "artikelErstellung";
	}

	@PostMapping("/account/{benutzername}/erstelleArtikel")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherArtikel(Model model, @PathVariable String benutzername, String daterange, Artikel artikel) {
		artikel.setVerfuegbarkeit(new Verfuegbarkeit(daterange));
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		model.addAttribute("link", "account/" + benutzername);
		return "backToTheFuture";
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

	@GetMapping("/detail/{id}")
	public String artikelDetail(Model model, @PathVariable Long id, Principal principal) {
		model.addAttribute("artikelDetail", artikelRepository.findById(id).get());
		model.addAttribute("aktuelleSeite", "Artikelansicht");
		model.addAttribute("angemeldet", principal != null);
		return "artikelDetail";
	}
}
