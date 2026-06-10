package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.dto.UtilisateurUpdateRequest;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UtilisateurService utilisateurService;

    @Test
    void findAll_DoitRetournerListe() {
        Utilisateur u1 = new Utilisateur();
        Utilisateur u2 = new Utilisateur();
        when(utilisateurRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Utilisateur> result = utilisateurService.findAll();

        assertEquals(2, result.size());
        verify(utilisateurRepository).findAll();
    }

    @Test
    void findById_QuandExiste_DoitRetournerUtilisateur() {
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));

        Utilisateur result = utilisateurService.findById(1L);

        assertNotNull(result);
        assertEquals(u, result);
    }

    @Test
    void findById_QuandExistePas_DoitLancerException() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> utilisateurService.findById(1L));
    }

    @Test
    void findByEmail_QuandExiste_DoitRetournerUtilisateur() {
        String email = "test@test.com";
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(u));

        Utilisateur result = utilisateurService.findByEmail(email);

        assertNotNull(result);
        assertEquals(u, result);
    }

    @Test
    void findByEmail_QuandExistePas_DoitLancerException() {
        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> utilisateurService.findByEmail("test@test.com"));
    }

    @Test
    void create_DoitSauvegarderUtilisateur() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@test.com");
        request.setAdresse("1 rue de Paris");

        Utilisateur saved = new Utilisateur();
        saved.setEmail("jean@test.com");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(saved);
        doNothing().when(emailService).sendMail(any(), any(), any());

        Utilisateur created = utilisateurService.create(request);

        assertNotNull(created);
        verify(utilisateurRepository).save(any(Utilisateur.class));
        verify(emailService).sendMail(any(), any(), any());
    }

    @Test
    void update_QuandExiste_DoitMettreAJourEtSauvegarder() {
        Long id = 1L;
        Utilisateur existing = new Utilisateur();
        existing.setId(id);
        existing.setNom("AncienNom");

        UtilisateurUpdateRequest details = new UtilisateurUpdateRequest();
        details.setNom("NouveauNom");

        when(utilisateurRepository.findById(id)).thenReturn(Optional.of(existing));
        when(utilisateurRepository.save(existing)).thenReturn(existing);

        Utilisateur updated = utilisateurService.update(id, details);

        assertEquals("NouveauNom", updated.getNom());
        verify(utilisateurRepository).save(existing);
    }

    @Test
    void update_QuandExistePas_DoitLancerException() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        UtilisateurUpdateRequest details = new UtilisateurUpdateRequest();
        assertThrows(RuntimeException.class, () -> utilisateurService.update(1L, details));
    }

    @Test
    void delete_QuandExiste_DoitSupprimer() {
        when(utilisateurRepository.existsById(1L)).thenReturn(true);

        utilisateurService.delete(1L);

        verify(utilisateurRepository).deleteById(1L);
    }

    @Test
    void delete_QuandExistePas_DoitLancerException() {
        when(utilisateurRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> utilisateurService.delete(1L));
    }

    @Test
    void softDelete_DoitPasserCompteActifAFalse() {
        Utilisateur u = new Utilisateur();
        u.setCompteActif(true);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));

        utilisateurService.softDelete(1L);

        assertFalse(u.getCompteActif());
        verify(utilisateurRepository).save(u);
    }

    @Test
    void reactivate_DoitPasserCompteActifATrue() {
        Utilisateur u = new Utilisateur();
        u.setCompteActif(false);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));

        utilisateurService.reactivate(1L);

        assertTrue(u.getCompteActif());
        verify(utilisateurRepository).save(u);
    }

    @Test
    void create_AvecRole_DoitSauvegarderAvecRole() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@test.com");
        request.setAdresse("1 rue de Paris");
        request.setRole("ADMIN");

        Utilisateur saved = new Utilisateur();
        saved.setEmail("jean@test.com");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> {
            Utilisateur arg = i.getArgument(0);
            assertEquals(Role.ADMIN, arg.getRole());
            return saved;
        });
        doNothing().when(emailService).sendMail(any(), any(), any());

        Utilisateur created = utilisateurService.create(request);

        assertNotNull(created);
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    void update_AvecTousChamps_DoitMettreAJourEtSauvegarder() {
        Long id = 1L;
        Utilisateur existing = new Utilisateur();
        existing.setId(id);
        existing.setNom("AncienNom");
        existing.setPrenom("AncienPrenom");
        existing.setEmail("ancien@test.com");
        existing.setAdresse("AncienneAdresse");

        UtilisateurUpdateRequest details = new UtilisateurUpdateRequest();
        details.setNom("NouveauNom");
        details.setPrenom("NouveauPrenom");
        details.setEmail("nouveau@test.com");
        details.setAdresse("NouvelleAdresse");

        when(utilisateurRepository.findById(id)).thenReturn(Optional.of(existing));
        when(utilisateurRepository.save(existing)).thenReturn(existing);

        Utilisateur updated = utilisateurService.update(id, details);

        assertEquals("NouveauNom", updated.getNom());
        assertEquals("NouveauPrenom", updated.getPrenom());
        assertEquals("nouveau@test.com", updated.getEmail());
        assertEquals("NouvelleAdresse", updated.getAdresse());
        verify(utilisateurRepository).save(existing);
    }

    @Test
    void update_AvecCertainsChampsNull_DoitMettreAJourUniquementNonNull() {
        Long id = 1L;
        Utilisateur existing = new Utilisateur();
        existing.setId(id);
        existing.setNom("AncienNom");
        existing.setPrenom("AncienPrenom");
        existing.setEmail("ancien@test.com");
        existing.setAdresse("AncienneAdresse");

        UtilisateurUpdateRequest details = new UtilisateurUpdateRequest();
        // prenom et adresse restent null, nom et email sont non-null
        details.setNom("NouveauNom");
        details.setEmail("nouveau@test.com");

        when(utilisateurRepository.findById(id)).thenReturn(Optional.of(existing));
        when(utilisateurRepository.save(existing)).thenReturn(existing);

        Utilisateur updated = utilisateurService.update(id, details);

        assertEquals("NouveauNom", updated.getNom());
        assertEquals("AncienPrenom", updated.getPrenom()); // inchangé
        assertEquals("nouveau@test.com", updated.getEmail());
        assertEquals("AncienneAdresse", updated.getAdresse()); // inchangé
        verify(utilisateurRepository).save(existing);
    }

    @Test
    void search_QuandTermeNullOuVide_DoitRetournerTout() {
        Utilisateur u1 = new Utilisateur();
        when(utilisateurRepository.findAll()).thenReturn(List.of(u1));

        List<Utilisateur> resultNull = utilisateurService.search(null);
        List<Utilisateur> resultBlank = utilisateurService.search("   ");

        assertEquals(1, resultNull.size());
        assertEquals(1, resultBlank.size());
        verify(utilisateurRepository, times(2)).findAll();
    }

    @Test
    void search_QuandTermeCorrespond_DoitFiltrer() {
        Utilisateur u1 = new Utilisateur();
        u1.setEmail("jean.dupont@test.com");
        u1.setNom("Dupont");
        u1.setPrenom("Jean");

        Utilisateur u2 = new Utilisateur(); // tout à null pour valider les null checks
        
        Utilisateur u3 = new Utilisateur();
        u3.setEmail("martin@test.com");
        u3.setNom("Martin");
        u3.setPrenom("Paul");

        when(utilisateurRepository.findAll()).thenReturn(Arrays.asList(u1, u2, u3));

        // Recherche par email
        List<Utilisateur> matchEmail = utilisateurService.search("dupont");
        assertEquals(1, matchEmail.size());
        assertEquals(u1, matchEmail.get(0));

        // Recherche par nom
        List<Utilisateur> matchNom = utilisateurService.search("Martin");
        assertEquals(1, matchNom.size());
        assertEquals(u3, matchNom.get(0));

        // Recherche par prenom
        List<Utilisateur> matchPrenom = utilisateurService.search("jean");
        assertEquals(1, matchPrenom.size());
        assertEquals(u1, matchPrenom.get(0));

        // Recherche sans correspondance
        List<Utilisateur> noMatch = utilisateurService.search("unknown");
        assertTrue(noMatch.isEmpty());
    }

    @Test
    void save_DoitAppelerRepository() {
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.save(u)).thenReturn(u);

        Utilisateur result = utilisateurService.save(u);

        assertEquals(u, result);
        verify(utilisateurRepository).save(u);
    }
}