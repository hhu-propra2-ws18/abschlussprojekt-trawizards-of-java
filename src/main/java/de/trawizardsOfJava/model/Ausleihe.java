package de.trawizardsOfJava.model;

import lombok.Data;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.cglib.core.GeneratorStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class Ausleihe {

    @Id
    @GeneratedValue
    private Long id;

    private String ausleihender;
    @OneToOne
    private Artikel artikel;

    private Verfuegbarkeit verfuegbarkeit;

}
