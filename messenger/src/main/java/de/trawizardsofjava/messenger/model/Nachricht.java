package de.trawizardsofjava.messenger.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Nachricht {
	@Id
	@GeneratedValue
	private Long id;
	private String nachricht;
	@OneToOne
	private Person absender;
	@OneToOne
	private Session session;

	private LocalDateTime gesendet;

	public String dateToString(){
		String s = "";
		s += this.gesendet.getDayOfMonth() +"/";
		s += this.gesendet.getMonth() + "/";
		s += this.gesendet.getYear() + " ";
		s += this.gesendet.getHour() + ":";
		s += this.gesendet.getMinute();

		return s;
	}
}
