package de.trawizardsOfJava.messenger.model;

import lombok.Data;

import javax.persistence.*;

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
}
