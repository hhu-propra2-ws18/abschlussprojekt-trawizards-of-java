package de.trawizardsOfJava.proPay;

import de.trawizardsOfJava.data.IProPaySchnittstelle;
import de.trawizardsOfJava.model.Ausleihe;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProPay {
	private Long amount;
	private List<Reservierung> reservations;
	private IProPaySchnittstelle proPaySchnittstelle;

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

	public Long letzteReservierung () {
		return reservations.get(reservations.size() - 1).getId();
	}
}