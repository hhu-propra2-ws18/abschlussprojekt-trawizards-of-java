package de.trawizardsOfJava.mail;

import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.data.RueckgabeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.security.Principal;

@Controller
public class MailController {

    @Autowired
    IMailService iMailService;

    @Autowired
    BenutzerRepository benutzerRepository;

    @GetMapping("/send/{id}")
    @ResponseBody

    public String home(@PathVariable Long id,  Principal principal){
        try {
            iMailService.sendEmailToKonfliktLoeseStelle(principal.getName(), id);
            return "Email erfolgreich gesendet!";
        }
        catch(MailException e){
            return "Email konnte nicht gesendet werden\n" + e.getMessage();
        }
    }
}
