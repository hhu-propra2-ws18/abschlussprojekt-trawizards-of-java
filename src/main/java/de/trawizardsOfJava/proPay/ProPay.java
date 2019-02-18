package de.trawizardsOfJava.proPay;

import lombok.Data;

import java.util.List;

@Data
public class ProPay {
	private int amount;
	private List<Reservierung> reservations;

	public int berechneVerfuegbaresGeld(){
		int reservierungsgeld = 0;
		for (Reservierung reservation : reservations) {
			reservierungsgeld += reservation.getAmount();
		}
		return amount - reservierungsgeld;
	}
}