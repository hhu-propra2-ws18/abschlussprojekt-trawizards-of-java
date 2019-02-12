package de.trawizardsOfJava.model;

import de.trawizardsOfJava.data.ArtikelRepository;
import de.trawizardsOfJava.data.BenutzerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@RunWith(SpringRunner.class)
@WebMvcTest
public class UebersichtseiteTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private BenutzerRepository benutzerRepository;

    @MockBean
    private ArtikelRepository artikelRepository;

    @Test
    public void retrieve() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk());
    }


}
