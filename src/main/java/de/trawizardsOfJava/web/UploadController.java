package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelKaufenRepository;
import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.ArtikelKaufen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Controller
public class UploadController {
	private static final String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/fotos";
	private ArtikelRepository artikelRepository;
	private ArtikelKaufenRepository artikelKaufenRepository;

	@Autowired
	public UploadController(ArtikelRepository artikelRepository, ArtikelKaufenRepository artikelKaufenRepository) {
		this.artikelRepository = artikelRepository;
		this.artikelKaufenRepository = artikelKaufenRepository;
	}

	@GetMapping("/fotoupload/{id}")
	public String index(Model model, @PathVariable Long id) {
		model.addAttribute("id", id);
		return "fotos_upload";
	}

	@PostMapping("/fotoupload/{id}")
	public String uploadMultipartFile(Model model, @RequestParam MultipartFile[] files, @PathVariable Long id) {
		for(MultipartFile file : files) {
			Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());

			if(artikelRepository.findById(id).isPresent()) {
				Artikel artikel = artikelRepository.findById(id).get();
				ArrayList<String> photoList = new ArrayList<>();
				photoList.add(fileNameAndPath.getFileName()+"");
				artikel.setFotos(photoList);

				artikelRepository.save(artikel);
			} else if(artikelKaufenRepository.findById(id).isPresent()) {
				ArtikelKaufen artikel = artikelKaufenRepository.findById(id).get();
				ArrayList<String> photoList = new ArrayList<>();
				photoList.add(fileNameAndPath.getFileName()+"");
				artikel.setFotos(photoList);

				artikelKaufenRepository.save(artikel);
			}
			try {
				Files.write(fileNameAndPath, file.getBytes());
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("link", "");
		return "backToTheFuture";
	}

}

