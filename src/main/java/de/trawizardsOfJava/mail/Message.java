package de.trawizardsOfJava.mail;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue
    private Long id;
    private String absender;
    private String empfaenger;
    private String nachricht;

    public Message(){}

    public Message(String absender, String empfaenger, String nachricht){
        this.absender = absender;
        this.empfaenger = empfaenger;
        this.nachricht = nachricht;
    }

    public static String generiereNachricht(String anlass, String person, String parameter){
        if(anlass.equals("AnfrageGestellt")){
            return String.format("Anfrage von %s für %s gestellt", person, parameter);
        }
        if(anlass.equals("AnfrageAngenommen")){
            return String.format("Anfrage von %s für %s wurde angommen", person, parameter);
        }
        if(anlass.equals("AnfrageAbgelehnt")){
            return String.format("Anfrage von %s für %s wurde abgelehnt", person, parameter);
        }
        if (anlass.equals("Rueckgabe")){
            return String.format("Der Artikel %s wurde von %s zurückgegeben.", parameter, person);
        }
        if (anlass.equals("RueckgabeAkzeptiert")){
            return String.format("Die Rückgabe des Artikels %s wurde von %s akzeptiert.", parameter, person);
        }
        if (anlass.equals("RueckgabeAbgelehnt")) {
            return String.format("Der Artikel %s wurde im mangelhaften Zustand zurückgegeben und wurde von %s an die Konfliktlösestelle übergeben.", parameter, person);
        }
        if (anlass.equals("Konflikt")){
            return String.format("Die Kaution für %s wurde %s zugeschrieben.", parameter, person);
        }
        return "";
    }
}
