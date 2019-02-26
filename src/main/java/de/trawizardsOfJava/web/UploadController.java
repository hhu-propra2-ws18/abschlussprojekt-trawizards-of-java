package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.FotoStorage;
import de.trawizardsOfJava.model.Artikel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UploadController {

	public static final String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/fotos";

	@Autowired
	FotoStorage fotoStorage;

	@Autowired
	private ArtikelRepository artikelRepository;

	@GetMapping("/fotoupload/{id}")
	public String index(Model model, @PathVariable long id) {
		model.addAttribute("id", id);
		return "fotos_upload";
	}


	@RequestMapping("/fotoupload/{id}")
	public String uploadMultipartFile(@RequestParam("files") MultipartFile[] files, Model model, @PathVariable long id) {

		try {
			for (MultipartFile file : files) {
				Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());

				Optional<Artikel> artikel = artikelRepository.findById(id);
				ArrayList<String> photoList = new ArrayList<>();
				photoList.add(fileNameAndPath.getFileName().toString());
				artikel.get().setFotos(photoList);

				artikelRepository.save(artikel.get());

				try {
					Files.write(fileNameAndPath, file.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}catch (NullPointerException exc){
			System.out.println(exc);
		}

		return "redirect:/";
	}

}

