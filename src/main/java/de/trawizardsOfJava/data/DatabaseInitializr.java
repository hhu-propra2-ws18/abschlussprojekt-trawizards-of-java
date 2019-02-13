package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class DatabaseInitializr implements ServletContextInitializer {
	@Autowired
	BenutzerRepository personen;
	
	@Override
	public void onStartup(ServletContext servletContext) {
		System.out.println("Populating the database");
		Person person = new Person();
		person.setBenutzername("root");
		person.setName("root");
		person.setEmail("root");
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		person.setPasswort(bCryptPasswordEncoder.encode("1234"));
		person.setRolle("ROLE_ADMIN");
		personen.save(person);
		Person person2 = new Person();
		person2.setBenutzername("joe");
		person2.setName("joe");
		person2.setEmail("joe");
		person2.setPasswort(bCryptPasswordEncoder.encode("1234"));
		person2.setRolle("ROLE_USER");
		personen.save(person2);
	}
	
}
