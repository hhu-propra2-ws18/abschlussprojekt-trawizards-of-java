package de.trawizardsOfJava.mail;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface MessageRepository extends CrudRepository<Message, Long> {

    ArrayList<Message> findById();

}
