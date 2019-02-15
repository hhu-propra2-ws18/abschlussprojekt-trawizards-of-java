package de.trawizardsOfJava.mail;

import de.trawizardsOfJava.data.BenutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class MailController {

    @Autowired
    IMailService iMailService;

    @Autowired
    BenutzerRepository benutzerRepository;

    @GetMapping("/send/{id}")
    public String home(@PathVariable Long id,  Principal principal){
        try {
            iMailService.sendEmailToKonfliktLoeseStelle(principal.getName(), id);
        }
        catch(MailException e){
            // Exception Handling
            System.out.println("Email konnte nicht gesendet werden\n" + e.getMessage());
        }
        return "zurueckgegebeneartikel";
    }
}
