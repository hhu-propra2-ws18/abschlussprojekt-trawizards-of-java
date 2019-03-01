package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.*;
import de.trawizardsOfJava.security.SecurityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class DatabaseInitializr implements ServletContextInitializer {
	@Autowired
	BenutzerRepository benutzerRepository;

	@Override
	public void onStartup(ServletContext servletContext) {
		System.out.println("Populating the database");

		Person person1 = new Person();
		person1.setBenutzername("root");
		person1.setEmail("root@mail.com");
		person1.setName("root");
		person1.setPasswort(SecurityConfig.encoder().encode("1234"));
		person1.setRolle("ROLE_ADMIN");
		benutzerRepository.save(person1);
	}
}
