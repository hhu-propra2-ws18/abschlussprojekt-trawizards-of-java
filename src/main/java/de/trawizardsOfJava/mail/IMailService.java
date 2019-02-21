package de.trawizardsOfJava.mail;

public interface IMailService {
	void sendEmailToKonfliktLoeseStelle(String name, String beschreibung, Long id);
	void sendReminder(String email, String name, String artikel);
}
