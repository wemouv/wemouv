package com.diginamic.wemouv.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secret = "1234567890123456789012345678901234567890"; // >= 256 bits key

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret);
    }

    @Test
    void generateToken_DoitGenererTokenValide() {
        UserDetails user = new User("test@wemouv.com", "password", Collections.emptyList());
        
        String token = jwtService.generateToken(user);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_DoitRetournerUsernameCorrect() {
        UserDetails user = new User("test@wemouv.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertEquals("test@wemouv.com", username);
    }

    @Test
    void isValid_QuandTokenCorrespond_DoitRetournerTrue() {
        UserDetails user = new User("test@wemouv.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void isValid_QuandTokenNeCorrespondPas_DoitRetournerFalse() {
        UserDetails user = new User("test@wemouv.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(user);

        UserDetails distinctUser = new User("other@wemouv.com", "password", Collections.emptyList());
        boolean isValid = jwtService.isValid(token, distinctUser);

        assertFalse(isValid);
    }
}
