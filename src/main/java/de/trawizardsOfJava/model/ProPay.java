package de.trawizardsOfJava.model;

import lombok.Data;

import java.util.List;

@Data
public class ProPay {

	String account;
	int amount;
	List<Reservierung> reservations;

}
