# 🎓 EduPlay

EduPlay est une application éducative développée en JavaFX destinée à gérer une plateforme interactive pour les étudiants. Elle comprend des modules comme les cours, l'inscription, l'évaluation, l'aide, le feedback, et plus encore.

## 🛠️ Technologies utilisées

- Java 17+
- JavaFX
- SceneBuilder
- SQLite / MySQL (au choix)
- Maven / Gradle
- QR Code (ZXing)
- GitHub pour la gestion de version

## 📚 Fonctionnalités

### 🔹 Accueil
Tableau de bord affichant des statistiques clés comme le nombre d'étudiants, d'évaluations, d'événements.

### 🔹 Cours
- CRUD de cours (Titre, Description, Date)
- Génération de QR Code pour chaque cours
- Affectation à des étudiants via un module d'inscription

### 🔹 Inscription
- Ajout d’un étudiant à un cours
- Choix du cours via un `ComboBox`
- Validation des dates

### 🔹 Évaluations
- Ajout de notes par étudiant
- Vue filtrée par cours

### 🔹 Feedback
- Les étudiants peuvent envoyer leur avis
- Visualisation par les administrateurs

### 🔹 Aide
- Centre d’assistance avec des articles ou des questions fréquentes
- Système de recherche intégré

### 🔹 Tickets
- Système de ticketing pour signaler un problème
- Suivi des statuts : Ouvert, En cours, Résolu

## 🧩 Architecture du projet

