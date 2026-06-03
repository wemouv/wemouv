package com.diginamic.wemouv.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class ReserverCovoiturageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void participerEndpoint() throws Exception {
        mockMvc.perform(post("/covoiturages/31/participer/7"))
                .andDo(print());
    }
}
