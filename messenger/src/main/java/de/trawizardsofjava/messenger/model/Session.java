package de.trawizardsofjava.messenger.model;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class Session {
	@EmbeddedId
	private Teilnehmer teilnehmer;
}
