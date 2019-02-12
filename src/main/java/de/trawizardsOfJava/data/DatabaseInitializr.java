package de.trawizardsOfJava.data;


import de.trawizardsOfJava.model.Artikel;
import de.trawizardsOfJava.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class DatabaseInitializr implements ServletContextInitializer {

    @Autowired
    ArtikelRepository artikel;

    @Autowired
    BenutzerRepository user;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("Populating the database");
    }

}
