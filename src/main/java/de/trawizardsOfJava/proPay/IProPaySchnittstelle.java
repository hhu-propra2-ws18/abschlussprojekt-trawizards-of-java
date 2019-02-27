package de.trawizardsOfJava.proPay;
import de.trawizardsOfJava.proPay.ProPay;

public interface IProPaySchnittstelle {
	ProPay getEntity(String benutzername);
	void post(String url);
	boolean ping();
}
