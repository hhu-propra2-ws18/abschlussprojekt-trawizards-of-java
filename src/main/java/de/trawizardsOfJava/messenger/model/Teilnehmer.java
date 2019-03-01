package de.trawizardsOfJava.messenger.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class Teilnehmer{
	@NotNull
	private String personEins;
	@NotNull
	private String personZwei;

	public Teilnehmer(){

	}

	public Teilnehmer(String personEins, String personZwei){
		this.personEins = personEins;
		this.personZwei = personZwei;
	}
}
