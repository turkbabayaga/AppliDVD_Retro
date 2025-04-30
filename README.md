# 🎬 AppliDVD_Retro &nbsp; ![Java](https://img.shields.io/badge/langage-Java-blue.svg) ![Android](https://img.shields.io/badge/platform-Android-green) ![Status](https://img.shields.io/badge/statut-en%20cours-orange)

**AppliDVD_Retro** est une application mobile Android conçue pour les vacanciers du **village vacances Rétro**.  
Elle leur permet de consulter le catalogue de DVD disponibles à la location, d’en afficher les détails, de les ajouter à un panier et de finaliser leur commande en toute simplicité.

---

## 🧾 Fonctionnalités principales

- 🔐 Authentification par email + mot de passe
- 🌐 Sélection dynamique de l'URL d'API (IP/port modifiables dans l'app)
- 📋 Affichage de la liste des DVD disponibles
- 📄 Détail complet d’un DVD (titre, année, description, etc.)
- 🛒 Ajout au panier, gestion des quantités
- ✅ Validation de la commande (requêtes POST vers l’API)
- 🔓 Déconnexion de l’utilisateur avec retour à la page de login

---

## 🏗️ Technologies utilisées

| Technologie        | Détail                          |
|--------------------|----------------------------------|
| 💻 Langage         | Java                            |
| ⚙️ Framework       | Android SDK                     |
| 📱 Version minimale Android | API 26 (Android 8.0)     |
| 🌐 Connexion API   | `HttpURLConnection` (GET / POST) |
| 💾 Stockage local  | `SharedPreferences`, `DonneesPartagees` |

---

## ⚙️ Installation

1. Clonez ce dépôt :
   ```bash
   git clone https://github.com/turkbabayaga/AppliDVD_Retro.git
