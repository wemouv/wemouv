package com.diginamic.wemouv.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UtilisateurUpdateRequestTest {

    @Test
    void testGettersAndSetters() {
        UtilisateurUpdateRequest request = new UtilisateurUpdateRequest();
        
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean.dupont@test.com");
        request.setAdresse("1 rue de Paris");

        assertEquals("Dupont", request.getNom());
        assertEquals("Jean", request.getPrenom());
        assertEquals("jean.dupont@test.com", request.getEmail());
        assertEquals("1 rue de Paris", request.getAdresse());
    }
}
