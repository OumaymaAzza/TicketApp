# Ticket App

Une application Android simple qui simule le traitement des commandes et l'impression de tickets pour un magasin de snacks. Cette application permet à l'utilisateur de visualiser une liste d'articles commandés et d'imprimer un reçu au format ESC/POS sur une imprimante thermique compatible via un réseau.

## Fonctionnalités :
- **Liste des commandes** : Affiche une liste d'articles avec leur nom, quantité et prix.
- **Génération du ticket** : Crée un reçu détaillant les articles commandés, les quantités, les prix, le sous-total, les frais de livraison et le total à payer.
- **Impression du ticket** : Permet d'envoyer le reçu à une imprimante thermique via une connexion réseau (IP et port de l'imprimante).
- **Mise en forme du ticket** : Utilisation des commandes ESC/POS pour formater le texte du ticket (alignement des colonnes, numéros de commande, messages personnalisés).

## Fonctionnement :
1. L'application affiche les articles d'une commande (données codées en dur pour l'exemple) dans un `RecyclerView`.
2. Lorsqu'un utilisateur appuie sur le bouton "Imprimer", les informations de la commande sont formatées en texte ESC/POS et envoyées à une imprimante réseau via un socket.
3. Le ticket est imprimé et un message de confirmation ou d'erreur est affiché à l'utilisateur.

## Technologies utilisées :
- **Kotlin** : Langage de programmation principal.
- **Kotlin Coroutines** : Gestion des tâches en arrière-plan (connexion au réseau pour l'impression).
- **ESC/POS** : Commandes pour le formatage des tickets d'impression.
- **RecyclerView** : Affichage dynamique de la liste des articles.

![image](https://github.com/user-attachments/assets/cdadb213-843e-4d24-b4b4-cb1967d5abe1)


