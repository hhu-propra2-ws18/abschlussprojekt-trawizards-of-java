package de.trawizardsOfJava.messenger.model;

import de.trawizardsOfJava.model.Person;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Embeddable
public class Teilnehmer implements Serializable {
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
