package de.trawizardsOfJava.model;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.nio.file.FileSystem;
import java.util.ArrayList;

@Data
@Entity
public class Artikel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String verleiherBenutzername;
	private String artikelName;
	private String beschreibung;
	private String standort;
	private int preis;
	private int kaution;
	private Verfuegbarkeit verfuegbarkeit;
	private ArrayList<String> fotos;
	//private FileSystemResource fotosFromVolume;
}