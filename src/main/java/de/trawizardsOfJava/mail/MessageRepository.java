package de.trawizardsOfJava.mail;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public interface MessageRepository extends CrudRepository<Message, Long> {
	ArrayList<Message> findAll();
	ArrayList<Message> findByEmpfaenger(String empfaenger);
}