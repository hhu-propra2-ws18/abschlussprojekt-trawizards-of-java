package de.trawizardsOfJava.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Reservierung {
	int amount;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int Id;
}
