package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
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

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class ArtikelController {
	private ArtikelRepository artikelRepository;
	private static final String ALTERNATIVE_PHOTO = "kein-bild-vorhanden.jpg";

	@Autowired
	public ArtikelController(ArtikelRepository artikelRepository) {
		this.artikelRepository = artikelRepository;
	}

	@ModelAttribute
	public void benutzername(Model model, Principal principal) {
		if(principal != null) {
			model.addAttribute("name", principal.getName());
		}
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
		model.addAttribute("photoId", id);
		return "artikelDetail";
	}

	@ResponseBody
	@RequestMapping(value = "/detail/{id}/foto", method = GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public FileSystemResource artikelDetailFoto(Model model, @PathVariable Long id, Principal principal) {
		model.addAttribute("artikelDetail", artikelRepository.findById(id).get());
		model.addAttribute("aktuelleSeite", "Artikelansicht");
		model.addAttribute("angemeldet", principal != null);

		if(!(artikelRepository.findById(id).get().getFotos().get(0).equals("fotos"))) {

			String photoUrl = artikelRepository.findById(id).get().getFotos().get(0);
			return new FileSystemResource("src/main/resources/fotos/" + photoUrl);
		}

		return new FileSystemResource("src/main/resources/fotos/" + ALTERNATIVE_PHOTO);
	}
}
