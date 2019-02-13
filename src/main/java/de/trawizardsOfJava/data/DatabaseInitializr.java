package de.trawizardsOfJava.data;


import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Person;
import de.trawizardsOfJava.model.Verfuegbarkeit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.time.LocalDate;

@Component
public class DatabaseInitializr implements ServletContextInitializer {

    @Autowired
    ArtikelRepository artikelRepository;

    @Autowired
    BenutzerRepository benutzerRepository;


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("Populating the database");

        Person person1 = new Person();
        person1.setBenutzername("root");
        person1.setEmail("root@mail.com");
        person1.setName("root");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        person1.setPasswort(bCryptPasswordEncoder.encode("1234"));
        person1.setRolle("ROLE_ADMIN");
        benutzerRepository.save(person1);

        Person person2 = new Person();
        person2.setBenutzername("joe");
        person2.setEmail("joe@mail.com");
        person2.setName("joe");
        person2.setPasswort(bCryptPasswordEncoder.encode("1234"));
        person2.setRolle("ROLE_USER");
        benutzerRepository.save(person2);

        Artikel artikel = new Artikel();
        artikel.setVerleiherName(person1);
        artikel.setArtikelName("Bagger");
        artikel.setBeschreibung("Dies ist ein Schaufelbagger");
        artikel.setKaution(2999);
        artikel.setPreis(149);
        artikel.setStandort("Mainz");
        String s = "02/01/2018 - 05/31/2019";
        Verfuegbarkeit verfuegbarkeit = new Verfuegbarkeit();
        verfuegbarkeit.toVerfuegbarkeit(s);
        artikel.setVerfuegbarkeit(verfuegbarkeit);
        artikelRepository.save(artikel);
    }

}
