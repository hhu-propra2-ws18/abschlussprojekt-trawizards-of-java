package de.trawizardsOfJava.mail;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue
    private Long id;
    private String absender;
    private String empfaenger;
    private String nachricht;

}
