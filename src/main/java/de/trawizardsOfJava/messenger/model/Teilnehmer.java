package de.trawizardsOfJava.messenger.model;

import de.trawizardsOfJava.model.Person;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class Teilnehmer{
	@NotNull
	@OneToOne
	private Person personEins;
	@NotNull
	@OneToOne
	private Person personZwei;

	public Teilnehmer(){

	}

	public Teilnehmer(Person personEins, Person personZwei){
		this.personEins = personEins;
		this.personZwei = personZwei;
	}
}
