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

	public void willkommensMail(String email){
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(leihe24Mail);
		mail.setTo(email);
		mail.setSubject("Willkommen bei Leih24!");
		mail.setText("Herzlich Willkommen bei Leih24!\n\nStöbern Sie auf unserer Seite " +
				"nach Artikeln, die Sie ausleihen oder kaufen möchten, oder stellen Sie Ihre eigenen " +
				"Artikel zum Verkauf hoch!\n\nViel Spaß, Ihr Leih24-Team");
		javaMailSender.send(mail);
	}

	public void sendReminder(String email, String name, String artikel) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(leihe24Mail);
		mail.setTo(email);
		mail.setSubject("Erinnerung: "+artikel+" zurückgeben!");
		mail.setText("Hallo "+name+",\n\nwir möchten Sie erinnern, "+
			"diesen ausgeliehenen Artikel bitte zügig zurückzugeben: "
			+artikel+"\n\nVielen Dank, Ihr Leih24-Team!");
		javaMailSender.send(mail);
	}
}