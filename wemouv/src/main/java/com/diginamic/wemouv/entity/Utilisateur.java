package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Role;
import jakarta.persistence.*;



@Entity
@Table(name = "utilisateur")
public class Utilisateur {


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

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}
}
