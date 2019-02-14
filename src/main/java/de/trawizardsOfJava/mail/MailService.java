package de.trawizardsOfJava.mail;

import de.trawizardsOfJava.data.BenutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService implements IMailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BenutzerRepository benutzerRepository;

    public MailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailToKonfliktLoeseStelle(String name, Long id) throws MailException{
        SimpleMailMessage mail = new SimpleMailMessage();
        //mail.setTo(benutzerRepository.findByBenutzername(name).get().getEmail());
        mail.setFrom("propra.leihe24@gmail.com");
        mail.setTo("propra.leihe24@gmail.com");
        mail.setText("Sehr geehrte Damen und Herren," + "%n%n" + "wir bitten um Auflösung des Konfliktes" +
                "der ID " + id + "%n%n" + "Vielen Dank.");
        mail.setSubject("Konflikt bei Ausleihe " + id);
        javaMailSender.send(mail);
    }
}
