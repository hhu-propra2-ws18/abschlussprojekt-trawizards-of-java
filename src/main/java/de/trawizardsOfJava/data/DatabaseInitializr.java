package de.trawizardsOfJava.data;


import de.trawizardsOfJava.model.*;
import de.trawizardsOfJava.web.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class DatabaseInitializr implements ServletContextInitializer {

    @Autowired
    ArtikelRepository artikelRepository;

    @Autowired
    BenutzerRepository benutzerRepository;

    @Autowired
    AusleiheRepository ausleiheRepository;

    @Autowired
    KonfliktRepository konfliktRepository;

    @Autowired
    RueckgabeRepository rueckgabeRepository;


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("Populating the database");

        Person person1 = new Person();
        person1.setBenutzername("root");
        person1.setEmail("root@mail.com");
        person1.setName("root");
        person1.setPasswort(SecurityConfig.encoder().encode("1234"));
        person1.setRolle("ROLE_ADMIN");
        benutzerRepository.save(person1);

        Person person2 = new Person();
        person2.setBenutzername("Joe");
        person2.setEmail("joe@mail.com");
        person2.setName("Joe");
        person2.setPasswort(SecurityConfig.encoder().encode("1234"));
        person2.setRolle("ROLE_USER");
        benutzerRepository.save(person2);

        Artikel artikel = new Artikel();
        artikel.setVerleiherBenutzername(person1.getBenutzername());
        artikel.setArtikelName("Bagger");
        artikel.setBeschreibung("Dies ist ein Schaufelbagger");
        artikel.setKaution(2999);
        artikel.setPreis(149);
        artikel.setStandort("Mainz");
        String s = "01/02/2018 - 31/05/2019";
        Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
        verfuegbarkeit.toVerfuegbarkeit(s);
        artikel.setVerfuegbarkeit(verfuegbarkeit);
        artikelRepository.save(artikel);

        Ausleihe ausleihe = new Ausleihe();
        ausleihe.setArtikel(artikel);
        ausleihe.setAusleihender(person2.getBenutzername());
        Verfuegbarkeit neues = new Verfuegbarkeit();
        neues.toVerfuegbarkeit("14/02/2019 - 16/02/2019");
        ausleihe.setVerfuegbarkeit(neues);
        ausleiheRepository.save(ausleihe);

        Rueckgabe rueckgabe = ausleihe.convertToRueckgabe();
        rueckgabeRepository.save(rueckgabe);

        Konflikt konflikt = new Konflikt();
        konflikt.setRueckgabe(rueckgabeRepository.findByVerleiherName(rueckgabe.getVerleiherName()).get(0));
        konflikt.setBeschreibung("kaputt");
        konfliktRepository.save(konflikt);

    }

}
