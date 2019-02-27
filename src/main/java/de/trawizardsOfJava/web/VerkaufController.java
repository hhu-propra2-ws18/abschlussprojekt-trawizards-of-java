package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.ArtikelKaufenRepository;
import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.AusleiheRepository;
import de.trawizardsOfJava.data.KaufRepository;
import de.trawizardsOfJava.mail.Message;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.Kauf;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import de.trawizardsOfJava.proPay.ProPaySchnittstelle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class VerkaufController {
    private IProPaySchnittstelle proPaySchnittstelle;
    private AusleiheRepository ausleiheRepository;
    private KaufRepository kaufRepository;
    private MessageRepository messageRepository;
    private ArtikelKaufenRepository artikelKaufenRepository;

    @Autowired
    public VerkaufController(MessageRepository messageRepository, IProPaySchnittstelle proPaySchnittstelle, AusleiheRepository ausleiheRepository, KaufRepository kaufRepository, ArtikelKaufenRepository artikelKaufenRepository) {
        this.proPaySchnittstelle = proPaySchnittstelle;
        this.ausleiheRepository = ausleiheRepository;
        this.kaufRepository = kaufRepository;
        this.messageRepository = messageRepository;
        this.artikelKaufenRepository = artikelKaufenRepository;
    }

    @ModelAttribute
    public void benutzername(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("name", principal.getName());
        }
    }

    @GetMapping("/account/{benutzername}/artikel/{id}/kaufen")
    @PreAuthorize("#benutzername == authentication.name")
    public String kaufen(Model model, @PathVariable String benutzername, @PathVariable Long id){
        return "kauf";
    }

    @PostMapping("/account/{benutzername}/artikel/{id}/kaufen")
    @PreAuthorize("#benutzername == authentication.name")
    public String bestaetigeKauf(Model model, @PathVariable String benutzername, @PathVariable Long id){
        if (!proPaySchnittstelle.ping()){
            model.addAttribute("proPayError", true);
            return kaufen(model, benutzername, id);
        }
        Kauf kauf = new Kauf(artikelKaufenRepository.findById(id).get(), benutzername);
        if (!proPaySchnittstelle.getEntity(benutzername).genuegendGeld((long) kauf.getArtikel().getPreis(), ausleiheRepository.findByAusleihenderAndAccepted(benutzername, false))) {
            model.addAttribute("error", true);
            return kaufen(model, benutzername, id);
        }
        kaufRepository.save(kauf);
        bezahlvorgang(kauf);
        artikelKaufenRepository.delete(kauf.getArtikel());
        messageRepository.save(new Message(kauf, "kaeufer"));
        messageRepository.save(new Message(kauf, "verkaeufer"));
        model.addAttribute("link", "");
        return "backToTheFuture";
    }

    private void bezahlvorgang(Kauf kauf) {
        if(!kauf.getVerkaeufer().equals(kauf.getKaeufer())) {
            proPaySchnittstelle.post("account/" + kauf.getKaeufer() + "/transfer/" + kauf.getVerkaeufer() + "?amount=" + kauf.getArtikel().getPreis());
        }
    }
}
