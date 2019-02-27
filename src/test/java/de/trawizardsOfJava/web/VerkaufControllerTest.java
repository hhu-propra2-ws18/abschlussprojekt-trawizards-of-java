package de.trawizardsOfJava.web;

import de.trawizardsOfJava.data.*;
import de.trawizardsOfJava.mail.MessageRepository;
import de.trawizardsOfJava.model.*;
import de.trawizardsOfJava.proPay.IProPaySchnittstelle;
import de.trawizardsOfJava.proPay.ProPay;
import de.trawizardsOfJava.security.SecurityPersonenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(VerkaufController.class)
public class VerkaufControllerTest {
	@Autowired
	MockMvc mvc;

	@MockBean
	private IProPaySchnittstelle proPaySchnittstelle;

	@MockBean
	private AusleiheRepository ausleiheRepository;

	@MockBean
	private MessageRepository messageRepository;

	@MockBean
	private ArtikelKaufenRepository artikelKaufenRepository;

	@MockBean
	private KaufRepository kaufRepository;

	@MockBean
	SecurityPersonenService securityPersonenService;

	@Test
	@WithMockUser(username = "bar")
	public void kaufen() throws Exception {

		ArtikelKaufen artikelKaufen = new ArtikelKaufen();
		artikelKaufen.setArtikelName("Schaufel");
		artikelKaufen.setPreis(10);
		artikelKaufen.setBeschreibung("Schaufel");
		artikelKaufen.setStandort("foo");
		artikelKaufen.setId(1L);
		artikelKaufen.setVerkaeufer("foo");
		Kauf kauf = new Kauf(artikelKaufen, "bar");

		ProPay proPay = new ProPay();
		proPay.setAmount(10L);
		proPay.setReservations(new ArrayList<>());

		when(artikelKaufenRepository.findById(1L)).thenReturn(Optional.of(artikelKaufen));
		when(proPaySchnittstelle.ping()).thenReturn(true);
		when(proPaySchnittstelle.getEntity("bar")).thenReturn(proPay);

		mvc.perform(post("/account/bar/artikel/" + 1L + "/kaufen"))
				.andExpect(view().name("backToTheFuture"));
		verify(kaufRepository).save(kauf);
	}
}