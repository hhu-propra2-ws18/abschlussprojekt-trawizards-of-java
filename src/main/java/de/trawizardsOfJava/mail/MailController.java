package de.trawizardsOfJava.mail;

import de.trawizardsOfJava.data.BenutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.MimeMessage;

@Controller
public class MailController {

    @Autowired
    JavaMailSender sender;

    @Autowired
    BenutzerRepository benutzerRepository;

    @GetMapping("/send")
    @ResponseBody
    public String hello(){
        try {
            sendWilkommensEmail();
            return "Email gesendet!";
        }
        catch(Exception e){
            return "Email konnte nicht gesendet werden";
        }
    }

    private void sendWilkommensEmail() throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        messageHelper.setTo(benutzerRepository.findByBenutzername("root").get().getEmail());
        messageHelper.setText("Hi");
        messageHelper.setSubject("Wilkommen bei Leih24!");
        sender.send(message);
    }

    @GetMapping("/send/konflikt/")
    @ResponseBody
    private String konfliktAbschicken(){
        return "";
    }
}
