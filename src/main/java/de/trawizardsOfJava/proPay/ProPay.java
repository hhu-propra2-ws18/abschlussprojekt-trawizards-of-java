package de.trawizardsOfJava.proPay;

import lombok.Data;

import java.util.List;

@Data
public class ProPay {
	private int amount;
	private List<Reservierung> reservations;
}