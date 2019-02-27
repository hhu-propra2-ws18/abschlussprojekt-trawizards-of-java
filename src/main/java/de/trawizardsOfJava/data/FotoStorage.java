package de.trawizardsOfJava.data;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FotoStorage implements FotoStorageInterface {

	private final Path rootLocation = Paths.get("fotos");
	private int index = 0;


	@Override
	public String store(MultipartFile file){
		System.out.println("TEST" + file);
		if (file.isEmpty()) {
			System.out.println("fileEmpty");
			return "nichts";
		}
		try {
			System.out.println("IM in try");
			String newName = "" + index;
			System.out.println("new Name" + newName);
			System.out.println("THIS"+Files.copy(file.getInputStream(), this.rootLocation.resolve(newName + ".jpg"), StandardCopyOption.REPLACE_EXISTING));
			Files.copy(file.getInputStream(), this.rootLocation.resolve(newName + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("image saved");
			index++;
			return newName;
		} catch (Exception e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}
}

