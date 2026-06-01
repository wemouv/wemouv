package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Role;
import jakarta.persistence.*;



@Entity
@Table(name = "utilisateur")
public class Utilisateur {



    @jakarta.persistence.Id
    private Long id1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String motDePasse;

    private String adresse;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean compteActif;



    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

}
