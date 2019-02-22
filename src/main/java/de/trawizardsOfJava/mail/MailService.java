package de.trawizardsOfJava.mail;

import de.trawizardsOfJava.data.BenutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService implements IMailService {
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private BenutzerRepository benutzerRepository;
	private String leihe24Mail = "propra.leihe24@gmail.com";

	public MailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendEmailToKonfliktLoeseStelle(String name, String beschreibung, Long id) throws MailException {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(benutzerRepository.findByBenutzername(name).get().getEmail());
		mail.setFrom(leihe24Mail);
		mail.setText("Sehr geehrte Damen und Herren,"+"\n\n"+"wir bitten um Auflösung des Konfliktes"+
			" der ID: "+id+"\n\n"+"Die Beschreibung des Geschädigten ist:\n\n"+beschreibung+
			"\nWir bitten um zügige Bearbeitung. \nVielen Dank.");
		mail.setSubject("Konflikt bei Ausleihe: "+id);
		javaMailSender.send(mail);
	}

	public void sendReminder(String email, String name, String artikel) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(leihe24Mail);
		mail.setTo(email);
		mail.setSubject("Erinnerung: "+artikel+" zurückgeben!");
		mail.setText("Sehr geehrter Herr "+name+",\n\nwir möchten Sie erinnern, "+
			"diesen ausgeliehenen Artikel bitte zügig zurückzugeben: "
			+artikel+"\n\nVielen Dank, Ihr Leihe24 Team!");
		javaMailSender.send(mail); //später wieder hinzufügen
	}
}