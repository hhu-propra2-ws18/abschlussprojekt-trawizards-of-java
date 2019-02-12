package com.example.trawizardsOfJava.data;


import com.example.trawizardsOfJava.model.Artikel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class DatabaseInitializr implements ServletContextInitializer {

    @Autowired
    ArtikelRepository artikel;


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("Populating the database");

        Artikel artikel1 = new Artikel();
        artikel1.setArtikelName("Hexler");
        artikel1.setVerleiherName("Marc");
        artikel.save(artikel1);

        Artikel artikel2 = new Artikel();
        artikel2.setArtikelName("Mini Cooper");
        artikel2.setVerleiherName("Kev");
        artikel.save(artikel2);

        Artikel artikel3 = new Artikel();
        artikel3.setArtikelName("Schlüsselanhänger");
        artikel3.setVerleiherName("Oli");
        artikel.save(artikel3);
    }

}
