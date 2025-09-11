````markdown
AGOUROU Laurent

# Mon Application Spring Boot

Cette application est développée en **Java avec Spring Boot**.  
Elle propose un système d’authentification sécurisé avec gestion des utilisateurs, formulaires Thymeleaf, et base de données relationnelle.
afin de développer une application web de gestion des viandes non acheté pour les revendre aux employés de l'entreprise

---

## ⚙️ Installation

### 1. Prérequis

Avant de commencer, assurez-vous d’avoir installé :

- [Java 23](https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html) ou version compatible
- [Maven](https://maven.apache.org/)
- [Git](https://git-scm.com/)
- Une base de données **MySQL** ou **PostgreSQL** (selon votre configuration)

### 2. Cloner le projet

```bash
git clone https://github.com/ton-compte/ton-projet.git
cd ton-projet
```
````

### 3. Configurer la base de données

- Créez une base de données (exemple : `app_db`)
- Ouvrez le fichier `application.properties` (ou `application.yml`) et configurez vos informations :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/app_db
spring.datasource.username=mon_user
spring.datasource.password=mon_password
spring.jpa.hibernate.ddl-auto=update
```

### 4. Lancer l’application

Avec Maven :

```bash
mvn spring-boot:run
```

Ou en lançant directement le fichier **main** depuis votre IDE (VS Code, IntelliJ, Eclipse).

---

## Utilisation

Une fois l’application lancée, ouvrez un navigateur et accédez à :

👉 [http://localhost:8080](http://localhost:8080)

Fonctionnalités disponibles :

- Page d’accueil
- Inscription d’un utilisateur
- Connexion et déconnexion
- Réinitialisation du mot de passe par e-mail
- Navigation sécurisée entre les pages

---

## Sécurité

- Authentification avec **Spring Security**
- Mots de passe encodés avec **BCrypt**
- Connexion sécurisée en **HTTPS**
- Réinitialisation du mot de passe via **Brevo** (ancien Sendinblue)

---

## Auteur

Projet développé dans le cadre du **Titre professionnel Concepteur Développeur d’Applications (CDA)**.

```

```
