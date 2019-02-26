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
    public VerkaufController(MessageRepository messageRepository, ProPaySchnittstelle proPaySchnittstelle, AusleiheRepository ausleiheRepository, KaufRepository kaufRepository, ArtikelKaufenRepository artikelKaufenRepository) {
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
    public String kaufen(Model model, @PathVariable("benutzername") String benutzername, @PathVariable("id") Long id){
        Kauf kauf = new Kauf(artikelKaufenRepository.findById(id).get(), benutzername);
        if (!proPaySchnittstelle.getEntity(benutzername).genuegendGeld(Long.valueOf(kauf.getArtikel().getPreis()), ausleiheRepository.findByAusleihenderAndAccepted(benutzername, false))) {
            model.addAttribute("error", true);
            return "/"; //Bei zu wenig Geld
        }
        kaufRepository.save(kauf);
        messageRepository.save(new Message(kauf, "angefragt"));
        messageRepository.save(new Message(kauf, "angefragtVerkäufer"));
        model.addAttribute("link", "");
        return "backToTheFuture";
    }

    /*private void bezahlvorgang(Kauf kauf) {
        kauf.setAccepted(true);
        if(!kauf.getVerkaeufer().equals(kauf.getKaeufer())) {
            Long tage = kauf.getVerfuegbarkeit().berechneZwischenTage();
            proPaySchnittstelle.post("account/" + ausleihe.getAusleihender() + "/transfer/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getPreis() * tage);
            proPaySchnittstelle.post("reservation/reserve/" + ausleihe.getAusleihender() + "/" + ausleihe.getVerleiherName() + "?amount=" + ausleihe.getArtikel().getKaution());
            ausleihe.setProPayId(proPaySchnittstelle.getEntity(ausleihe.getAusleihender()).letzteReservierung());
        }
    }*/

    @PostMapping("/account/{benutzername}/kaufanfragen")
    @PreAuthorize("#benutzername == authentication.name")
    public String kaufAkzeptiert(Model model, @PathVariable String benutzername, Long id, String accepted) {
        Kauf kauf = kaufRepository.findById(id).get();
        if("angenommen".equals(accepted)) kauf.setAccepted(true);
        if("abgelehnt".equals(accepted)) kauf.setAccepted(false);
        messageRepository.save(new Message(kauf, accepted));
        model.addAttribute("link", "account/" + benutzername + "/anfragenuebersicht");
        return "backToTheFuture";
    }
}
