package de.trawizardsOfJava.proPay;

import de.trawizardsOfJava.model.Ausleihe;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProPay {
	private int amount;
	private List<Reservierung> reservations;

	private int berechneVerfuegbaresGeld(){
		int reservierungsgeld = 0;
		for (Reservierung reservation : this.reservations) {
			reservierungsgeld += reservation.getAmount();
		}
		return this.amount - reservierungsgeld;
	}

	public boolean genuegendGeld(int gebrauchtesGeld, ArrayList<Ausleihe> anfragen){
		int verfuegbaresGeld = this.berechneVerfuegbaresGeld();
		for (Ausleihe anfrage : anfragen) {
			gebrauchtesGeld += anfrage.berechneGesamtPreis();
		}
		return verfuegbaresGeld >= gebrauchtesGeld;
	}

	public static int bezahlvorgang(Ausleihe ausleihe){
		int tage = ausleihe.getVerfuegbarkeit().berechneZwischenTage();
		ProPaySchnittstelle.post("account/" + ausleihe.getAusleihender() + "/transfer/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getPreis() * tage);
		ProPaySchnittstelle.post("reservation/reserve/" + ausleihe.getAusleihender() + "/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getKaution());
		List<Reservierung> reservierungen = ProPaySchnittstelle.getEntity(ausleihe.getAusleihender()).getReservations();
		return reservierungen.get(reservierungen.size() - 1).getId();
	}
}