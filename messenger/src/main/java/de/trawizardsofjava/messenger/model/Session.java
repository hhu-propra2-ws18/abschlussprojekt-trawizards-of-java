package de.trawizardsofjava.messenger.model;

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
}
