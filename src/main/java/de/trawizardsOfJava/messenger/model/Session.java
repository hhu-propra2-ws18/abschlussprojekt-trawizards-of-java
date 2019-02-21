package de.trawizardsOfJava.messenger.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Session  implements Serializable {
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
