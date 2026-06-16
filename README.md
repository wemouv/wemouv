[![Mise en place CI/CD](https://github.com/wemouv/wemouv/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/wemouv/wemouv/actions/workflows/ci-cd.yml)
[![Mise en place CI/CD](https://github.com/wemouv/wemouv/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/wemouv/wemouv/actions/workflows/ci-cd.yml)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=wemouv_wemouv&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=wemouv_wemouv)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=wemouv_wemouv&metric=coverage)](https://sonarcloud.io/summary/new_code?id=wemouv_wemouv)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=wemouv_wemouv&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=wemouv_wemouv)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=wemouv_wemouv&metric=bugs)](https://sonarcloud.io/summary/new_code?id=wemouv_wemouv)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=wemouv_wemouv&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=wemouv_wemouv)
# ☕ Wemouv - API (Back-end)

### 🔐 Authentification & Rôles
* **Sécurité renforcée** : Authentification basée sur les JSON Web Tokens (JWT) avec Spring Security.
* **Gestion des rôles** : Distinction entre les rôles `USER` (collaborateur/conducteur/passager) et `ADMIN` (gestionnaire de flotte/administrateur).
* **Inscription & Activation** : Enregistrement des utilisateurs avec contrôle d'activation de compte.

### 🚗 Gestion du Covoiturage
* **Publication d'annonces** : Les collaborateurs peuvent proposer des trajets de covoiturage (départ, destination, date/heure, nombre de places).
* **Recherche & Réservation** : Recherche de trajets disponibles et participation en tant que passager (mise à jour dynamique des places disponibles).
* **Véhicules Personnels** : Gestion des véhicules propres à chaque collaborateur associés à leurs offres de covoiturage.

### 🏢 Réservation de Véhicules de Service (Flotte d'Entreprise)
* **Réservation de véhicules** : Emprunt de véhicules de service pour des besoins professionnels (plages horaires définies).
* **Suivi de l'état du parc** : Visualisation en temps réel de la disponibilité des véhicules (`DISPONIBLE`, `EN_REPARATION`, `HORS_SERVICE`).
* **Gestion administrative** : Ajout, modification et retrait de véhicules de service par l'administrateur (marque, modèle, catégorie, immatriculation, motorisation).

### 📧 Notifications par Email
* Envoi d'emails transactionnels (confirmations d'inscription, rappels de réservation ou covoiturage) grâce à l'intégration SMTP (Gmail).

---

## 🌐 Accès en Ligne

L'application est déployée et accessible publiquement via le lien suivant :

👉 Web : **[https://wemouv-frontend.onrender.com/](https://wemouv-frontend.onrender.com/)**

---

## 🛠️ Stack Technique

Le projet repose sur un écosystème moderne de développement Java/Spring :

| Technologie | Version / Détails | Rôle |
| :--- | :--- | :--- |
| **Java** | 21 (LTS) | Langage de programmation principal |
| **Spring Boot** | 3.2.5 | Framework d'application de base |
| **Spring Security** | Via JWT | Sécurisation des endpoints de l'API |
| **Spring Data JPA** | Hibernate | ORM et persistance des données |
| **Base de Données** | MySQL / H2 | Stockage des données (MySQL en dev/prod, H2 en test) |
| **Documentation API** | Springdoc OpenAPI (Swagger UI) | Documentation et test des endpoints REST |
| **Lombok** | 1.18.x | Simplification du code Java (Boilerplate reduction) |
| **Mails** | Spring Mail (Thymeleaf) | Moteur de rendu et envoi de courriels |
| **Qualité / CI-CD** | JaCoCo, SpotBugs, Checkstyle | Pipeline de validation et métriques |

---

## ⚙️ Configuration & Installation

### Prérequis
* **Java 21** installé et configuré (`JAVA_HOME`)
* **Maven 3.8+**
* Un serveur **MySQL** actif (local ou distant)

### Configuration des environnements
L'application utilise les profils Spring pour s'adapter à l'environnement d'exécution :
* `dev` : Utilise une base de données de développement et les configurations locales.
* `test` : Utilise une base de données de test H2 en mémoire.
* `prod` : Configuré pour le déploiement de production (variables d'environnement).

#### Fichier `application-dev.properties`
Pour démarrer en local, assurez-vous d'avoir configuré vos accès dans `wemouv/src/main/resources/application-dev.properties` (base de données MySQL de dev, variables d'envoi d'email Gmail, secret JWT).

### Démarrage Local

1. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/wemouv/wemouv.git
   cd wemouv
   ```

2. **Compiler et installer les dépendances** :
   ```bash
   mvn clean install
   ```

3. **Lancer les tests unitaires et d'intégration** :
   ```bash
   mvn verify
   ```

4. **Lancer l'application** (dans le sous-dossier `wemouv`) :
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

L'API sera disponible par défaut à l'adresse : `http://localhost:8080` (ou le port défini dans vos propriétés).

---

## 📖 Documentation de l'API (Swagger UI)

Une fois l'application démarrée, vous pouvez visualiser et tester l'ensemble des endpoints REST en accédant à l'interface Swagger :

👉 Local : **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

👉 Web : **[https://wemouv.onrender.com/swagger-ui/index.html](https://wemouv.onrender.com/swagger-ui/index.html)**



---

## 🐳 Docker & Déploiement

Un `Dockerfile` est fourni à la racine du module `wemouv` pour conteneuriser l'application en mode production :

1. **Construire l'image Docker** :
   ```bash
   docker build -t wemouv-api ./wemouv
   ```

2. **Lancer le conteneur** :
   ```bash
   docker run -d -p 10000:10000 --name wemouv-app \
     -e DATABASE_URL=jdbc:mysql://YOUR_DB_HOST:3006/db_name \
     -e DATABASE_USERNAME=your_user \
     -e DATABASE_PASSWORD=your_pass \
     -e JWT_SECRET=your_super_secret_key \
     wemouv-api
   ```

---

## 🚀 Pipeline CI/CD & Qualité

Le projet intègre une pipeline d'intégration et déploiement continus (CI/CD) automatisée via GitHub Actions (`.github/workflows/ci-cd.yml`) :

* **Build & Test** : Vérification systématique du code sous Java 21 à chaque Push sur `main`.
* **Qualité du Code** :
  * **Checkstyle** (règles Google) : Garantie du respect des normes de formatage du code.
  * **SpotBugs** : Analyse statique pour détecter de potentiels bugs logiques.
  * **SonarCloud** : Analyse approfondie de la qualité du code (Quality Gate).
  * **CodeQL** : Scan de sécurité intégré pour détecter les vulnérabilités.
* **Taux de Couverture (JaCoCo)** : Analyse de la couverture des tests (seuil minimal fixé à 50%).
* **Génération de Rapports** : Sauvegarde et publication automatique des rapports de test et couverture (JUnit & JaCoCo) comme artéfacts GitHub.
Bienvenue sur le code source de l'API Spring Boot du projet Wemouv. 
