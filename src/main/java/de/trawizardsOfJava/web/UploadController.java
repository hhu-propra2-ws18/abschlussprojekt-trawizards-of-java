package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.FotoStorage;
import de.trawizardsOfJava.model.Artikel;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UploadController {
	public static String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/fotos";
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

		StringBuilder fileNames = new StringBuilder();
		for (MultipartFile file : files) {
			Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
			fileNames.append(file.getOriginalFilename()+" ");
			System.out.println("Name of File" + fileNameAndPath.getFileName());

			Optional<Artikel> artikel = artikelRepository.findById(id);
			ArrayList<String> test = new ArrayList<>();
			test.add(fileNameAndPath.getFileName().toString());
			artikel.get().setFotos(test);

			artikelRepository.save(artikel.get());

			try {
				Files.write(fileNameAndPath, file.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		//model.addAttribute("msg", "Successfully uploaded files "+fileNames.toString());

		return "redirect:/";
	}

}

