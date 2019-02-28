package de.trawizardsOfJava.messenger.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;

@Data
@Entity
public class Session{
	@Id
	@GeneratedValue
	private Long id;
	@Embedded
	private Teilnehmer teilnehmer;

	public Session(){
	}

	public Session(Teilnehmer teilnehmer){
		this.teilnehmer = teilnehmer;
	}

	public Long isExisting(ArrayList<Session> sessions){
		Teilnehmer tausch = new Teilnehmer(teilnehmer.getPersonZwei(), teilnehmer.getPersonEins());
		for (Session session:sessions){
			if(session.getTeilnehmer().equals(teilnehmer)){
				return session.getId();
			}
			if(session.getTeilnehmer().equals(tausch)){
				return session.getId();
			}
		}
		return Long.valueOf(-1);
	}

	public String toString(String user){
		if (user.equalsIgnoreCase(teilnehmer.getPersonEins())){
			return teilnehmer.getPersonZwei();
		}
		return teilnehmer.getPersonEins();
	}
}
