package de.trawizardsOfJava.web;

import de.trawizardsOfJava.model.ProPay;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ControllerLogik {

	public static ProPay getEntity(String benutzername){
		try {
			final Mono<ProPay> mono = WebClient
					.create("localhost:8888/account/" + benutzername)
					.get()
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(ProPay.class);
			return mono.block();
		}catch (Exception e){
			System.err.println("ERROR" + e);
			return null;
		}
	}

	public static void post(String url){
		try {
			WebClient.create("localhost:8888/" + url)
				.post()
				.retrieve()
				.bodyToMono(Object.class)
				.block();
		}catch (Exception e){
			System.err.println("ERROR" + e);
		}

	}

}
