package de.trawizardsOfJava.mail;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, Long> {

    ArrayList<Message> findByBenutzername(String benutzername);

    ArrayList<Message> findById();

}
