package com.diginamic.wemouv.security;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_QuandUtilisateurExiste_DoitRetournerUserDetails() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@wemouv.com");
        utilisateur.setMotDePasse("hashed_password");
        utilisateur.setRole(Role.USER);

        when(utilisateurRepository.findByEmail("test@wemouv.com")).thenReturn(Optional.of(utilisateur));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@wemouv.com");

        assertNotNull(userDetails);
        assertEquals("test@wemouv.com", userDetails.getUsername());
        assertEquals("hashed_password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        
        verify(utilisateurRepository).findByEmail("test@wemouv.com");
    }

    @Test
    void loadUserByUsername_QuandUtilisateurNExistePas_DoitLeverException() {
        when(utilisateurRepository.findByEmail("unknown@wemouv.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("unknown@wemouv.com"));

        verify(utilisateurRepository).findByEmail("unknown@wemouv.com");
    }
}
