package com.diginamic.wemouv.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_QuandPasDEnTeteAuthorization_DoitPasserAuFiltreSuivant() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_QuandEnTeteNeCommencePasParBearer_DoitPasserAuFiltreSuivant() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_QuandTokenValide_DoitAuthentifierLUtilisateur() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(jwtService.extractUsername("valid_token")).thenReturn("user@wemouv.com");

        UserDetails userDetails = new User("user@wemouv.com", "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("user@wemouv.com")).thenReturn(userDetails);
        when(jwtService.isValid("valid_token", userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@wemouv.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_QuandTokenInvalide_DoitNePasAuthentifierLUtilisateur() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        when(jwtService.extractUsername("invalid_token")).thenReturn("user@wemouv.com");

        UserDetails userDetails = new User("user@wemouv.com", "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("user@wemouv.com")).thenReturn(userDetails);
        when(jwtService.isValid("invalid_token", userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
