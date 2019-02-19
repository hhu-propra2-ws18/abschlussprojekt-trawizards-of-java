package de.trawizardsofjava.messenger.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
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

}
