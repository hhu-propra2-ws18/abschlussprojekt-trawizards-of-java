package de.trawizardsOfJava.data;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.time.LocalDate;

@Component
public class DatabaseInitializr implements ServletContextInitializer {

    @Autowired
    ArtikelRepository artikel;


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("Populating the database");

    }

}
