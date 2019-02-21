package de.trawizardsOfJava.proPay;

import de.trawizardsOfJava.model.Ausleihe;
import lombok.Data;

import java.util.ArrayList;
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

	public boolean genuegendGeld(int gebrauchtesGeld, ArrayList<Ausleihe> anfragen){
		int verfuegbaresGeld = berechneVerfuegbaresGeld();
		for (Ausleihe anfrage : anfragen) {
			gebrauchtesGeld += anfrage.berechneGesamtPreis();
		}
		return verfuegbaresGeld >= gebrauchtesGeld;
	}
}