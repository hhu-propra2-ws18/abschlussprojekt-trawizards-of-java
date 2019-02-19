package de.trawizardsofjava.messenger.model;

import lombok.Data;

import javax.persistence.*;

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
}
