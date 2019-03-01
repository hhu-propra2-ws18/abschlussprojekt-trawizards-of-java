package de.trawizardsOfJava.mail;

public interface IMailService {
	void willkommensMail(String email);
	void sendReminder(String email, String name, String artikel);
}
