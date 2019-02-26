package de.trawizardsOfJava.messenger.model;

import de.trawizardsOfJava.model.Person;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Nachricht {
	@Id
	@GeneratedValue
	private Long id;
	private String chat;
	@OneToOne
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private Person absender;
	@OneToOne
	private Session session;

	private LocalDateTime gesendet;
	
	public Nachricht(){
	
	}
	
	public Nachricht(Person absender, Session session){
		this.absender = absender;
		this.session = session;
	}

	public String dateToString(){
		if (this.gesendet == null)return "Kein Datum";
		String s = "";
		s += this.gesendet.getHour() + ":";
		s += this.gesendet.getMinute() + " ";
		s += this.gesendet.getDayOfMonth() +"/";
		s += this.gesendet.getMonth() + "/";
		s += this.gesendet.getYear();
		
		return s;
	}
}
