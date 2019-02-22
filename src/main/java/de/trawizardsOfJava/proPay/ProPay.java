package de.trawizardsOfJava.proPay;

import de.trawizardsOfJava.model.Ausleihe;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProPay {
	private Long amount;
	private List<Reservierung> reservations;

	private Long berechneVerfuegbaresGeld(){
		Long reservierungsgeld = 0L;
		for (Reservierung reservation : this.reservations) {
			reservierungsgeld += reservation.getAmount();
		}
		return this.amount - reservierungsgeld;
	}

	public boolean genuegendGeld(Long gebrauchtesGeld, ArrayList<Ausleihe> anfragen){
		Long verfuegbaresGeld = this.berechneVerfuegbaresGeld();
		for (Ausleihe anfrage : anfragen) {
			gebrauchtesGeld += anfrage.berechneGesamtPreis();
		}
		return verfuegbaresGeld >= gebrauchtesGeld;
	}

	public static void bezahlvorgang(Ausleihe ausleihe) {
		ausleihe.setAccepted(true);
		if(!ausleihe.getVerleiherName().equals(ausleihe.getAusleihender())) {
			Long tage = ausleihe.getVerfuegbarkeit().berechneZwischenTage();
			ProPaySchnittstelle.post("account/" + ausleihe.getAusleihender() + "/transfer/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getPreis() * tage);
			ProPaySchnittstelle.post("reservation/reserve/" + ausleihe.getAusleihender() + "/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getKaution());
			List<Reservierung> reservierungen = ProPaySchnittstelle.getEntity(ausleihe.getAusleihender()).getReservations();
			ausleihe.setProPayId(reservierungen.get(reservierungen.size() - 1).getId());
		}
	}
}