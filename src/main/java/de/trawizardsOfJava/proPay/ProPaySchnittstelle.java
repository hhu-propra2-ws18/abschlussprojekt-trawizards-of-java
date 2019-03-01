package de.trawizardsOfJava.proPay;

import io.netty.channel.ChannelOption;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.net.*;

@Component
public class ProPaySchnittstelle implements IProPaySchnittstelle {
	@Override
	public ProPay getEntity(String benutzername) {
		for (int versuche = 0; versuche <= 3; versuche++) {
			try {
				HttpClient httpClient = HttpClient.create().tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000));
				WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
				ProPay proPay = webClient.get()
						.uri("propay:8888/account/" + benutzername)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.retrieve().bodyToMono(ProPay.class)
						.block();
				return proPay;
			} catch (Exception e) {
				if (++versuche == 3) {
					System.err.println("ERROR: " + e);
				}
			}
		}
		return new ProPay();
	}

	@Override
	public void post(String url) {
		for (int versuche = 0; versuche <= 3; versuche++) {
			try {
				HttpClient httpClient = HttpClient.create().tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000));
				WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
				webClient.post().uri("propay:8888/" + url)
						.retrieve()
						.bodyToMono(Object.class)
						.block();
				return;
			} catch (Exception e) {
				if (++versuche == 3) {
					System.err.println("ERROR: " + e);
				}
			}
		}
	}

	public boolean ping() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress("propay", 8888), 5000);
			return true;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}