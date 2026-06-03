package com.diginamic.wemouv.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class RechercheCovoiturageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RechercheCovoiturage rechercheCovoiturage;

    @Test
    void rechercherService_parDepart() {
        rechercheCovoiturage.rechercher("Paris", null, null, null);
    }

    @Test
    void rechercherEndpoint_parDepart() throws Exception {
        mockMvc.perform(get("/covoiturages/recherche").param("depart", "Paris"))
                .andDo(print());
    }

    @Test
    void rechercherEndpoint_parStatutConfirme() throws Exception {
        mockMvc.perform(get("/covoiturages/recherche").param("statut", "CONFIRME"))
                .andDo(print());
    }
}
