package de.trawizardsOfJava.proPay;

public interface IProPaySchnittstelle {
	ProPay getEntity(String benutzername);
	void post(String url);
	boolean ping();
}
