package de.trawizardsOfJava.data;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorageInterface {
	public String store(MultipartFile file);
}