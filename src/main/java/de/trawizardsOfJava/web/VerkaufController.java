package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.KaufRepository;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Kauf;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import de.trawizardsOfJava.proPay.ProPaySchnittstelle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Properties;

@Controller
public class VerkaufController {
    private IProPaySchnittstelle proPaySchnittstelle;
    private ArtikelRepository artikelRepository;
    private AusleiheRepository ausleiheRepository;
    private KaufRepository kaufRepository;
    private MessageRepository messageRepository;

    @Autowired
    public VerkaufController(MessageRepository messageRepository, ProPaySchnittstelle proPaySchnittstelle, ArtikelRepository artikelRepository, AusleiheRepository ausleiheRepository, KaufRepository kaufRepository) {
        this.proPaySchnittstelle = proPaySchnittstelle;
        this.artikelRepository = artikelRepository;
        this.ausleiheRepository = ausleiheRepository;
        this.kaufRepository = kaufRepository;
        this.messageRepository = messageRepository;
    }

    @ModelAttribute
    public void benutzername(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("name", principal.getName());
        }
    }

    @GetMapping("/account/{benutzername}/artikel/{id}/kaufen")
    public String kaufen(Model model, @PathVariable String benutzername, @PathVariable Long id){
        Kauf kauf = new Kauf(artikelRepository.findById(id).get(), String benutzername);
        if (!proPaySchnittstelle.getEntity(benutzername).genuegendGeld(Long.valueOf(kauf.getArtikel().getPreis()), ausleiheRepository.findByAusleihenderAndAccepted(benutzername, false))) {
            model.addAttribute("error", true);
            return "/"; //Bei zu wenig Geld
        }
        kaufRepository.save(kauf);
        messageRepository.save(new Message(kauf));
        model.addAttribute("link", "account/" + benutzername + "/nachrichten");
        return "backToTheFuture";
    }
}
