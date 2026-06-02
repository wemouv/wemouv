package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public Utilisateur create(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur update(Long id,Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }

    /**
     * compteActif doit être désactivé
     * @param id
     */
    public void softDelete(Long id) {
        utilisateurRepository.findById(id).ifPresent(u -> {
            u.setCompteActif(false);
            utilisateurRepository.save(u);
        });
    }
}

