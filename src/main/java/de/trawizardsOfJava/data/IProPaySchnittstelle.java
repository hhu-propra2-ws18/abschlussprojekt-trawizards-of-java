package de.trawizardsOfJava.data;
import de.trawizardsOfJava.proPay.ProPay;

public interface IProPaySchnittstelle {
	ProPay getEntity(String benutzername);
	void post(String url);
}
