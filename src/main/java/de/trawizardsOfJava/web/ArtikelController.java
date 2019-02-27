package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelKaufenRepository;
import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.ArtikelKaufen;
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
	private ArtikelKaufenRepository artikelKaufenRepository;

	@Autowired
	public ArtikelController(ArtikelRepository artikelRepository, ArtikelKaufenRepository artikelKaufenRepository) {
		this.artikelRepository = artikelRepository;
		this.artikelKaufenRepository = artikelKaufenRepository;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if (principal != null) {
			model.addAttribute("name", principal.getName());
		}
	}

	@GetMapping("/account/{benutzername}/select")
	@PreAuthorize("#benutzername == authentication.name")
	public String select(Model model, @PathVariable String benutzername){
		model.addAttribute("select", "");
		return "select";
	}

	@PostMapping("/account/{benutzername}/select")
	@PreAuthorize("#benutzername == authentication.name")
	public String postSelect(Model model, @PathVariable String benutzername, String select){
		if("Verkaufen".equals(select)){
			model.addAttribute("link", "account/" + benutzername + "/erstelleArtikel/kaufen");
			return "backToTheFuture";
		}
		model.addAttribute("link", "account/" + benutzername + "/erstelleArtikel/leihen");
		return "backToTheFuture";
	}


	@GetMapping("/account/{benutzername}/erstelleArtikel/leihen")
	@PreAuthorize("#benutzername == authentication.name")
	public String erstelleArtikel_leihen(Model model, @PathVariable String benutzername) {
		model.addAttribute("artikel", new Artikel());
		model.addAttribute("verkaufen", false);
		return "artikelErstellung";
	}

	@PostMapping("/account/{benutzername}/erstelleArtikel/leihen")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherArtikel(Model model, @PathVariable String benutzername, String daterange, Artikel artikel) {
		artikel.setVerfuegbarkeit(new Verfuegbarkeit(daterange));
		artikel.setVerleiherBenutzername(benutzername);
		artikelRepository.save(artikel);
		model.addAttribute("link", "account/" + benutzername);
		return "backToTheFuture";
	}

	@GetMapping("/account/{benutzername}/erstelleArtikel/kaufen")
	@PreAuthorize("#benutzername == authentication.name")
	public String erstelleArtikel_kaufen(Model model, @PathVariable String benutzername) {
		model.addAttribute("artikel", new ArtikelKaufen());
		model.addAttribute("verkaufen", true);
		return "artikelErstellung";
	}

	@PostMapping("/account/{benutzername}/erstelleArtikel/kaufen")
	@PreAuthorize("#benutzername == authentication.name")
	public String speicherArtikelKaufen(Model model, @PathVariable String benutzername, ArtikelKaufen artikel) {
		artikel.setVerkaeufer(benutzername);
		artikelKaufenRepository.save(artikel);
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
		if (artikelRepository.findById(id).isPresent()) {
			model.addAttribute("artikelDetail", artikelRepository.findById(id).get());
			model.addAttribute("verkaufen", false);
		}
		else {
			model.addAttribute("artikelDetail", artikelKaufenRepository.findById(id).get());
			model.addAttribute("verkaufen", true);
		}
		model.addAttribute("aktuelleSeite", "Artikelansicht");
		model.addAttribute("angemeldet", principal != null);
		return "artikelDetail";
	}
}
