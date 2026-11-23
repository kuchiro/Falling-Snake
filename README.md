# Falling Snake

*Un dérivé du jeu Snake écrit en Java (Swing) : cette fois, c'est toi qui joues le canon. Le serpent part en haut à gauche et descend d'une ligne à chaque bord du plateau. Détruis-le avant qu'il n'arrive en bas !*

## Compilation

*Il faut avoir Java installé (JDK 8 ou plus récent). Dans le dossier du projet, compile le jeu avec :*

```bash
javac App.java
```

*Java compile automatiquement toutes les autres classes dont `App` a besoin.*

## Lancer le jeu

*Une fois compilé, lance le jeu avec :*

```bash
java App
```

*La fenêtre s'ouvre sur le menu de choix de la difficulté.*

## Les touches

### Dans le menu

*Clique sur un niveau pour lancer la partie, ou choisis-le avec les flèches ↑ ↓ puis valide avec Entrée (ou Espace).*

### Pendant la partie

| Touche | Action |
|---|---|
| ← → | Déplacer le canon |
| Espace ou ↑ | Tirer |
| P ou Échap | Mettre en pause (ou clic sur le bouton pause en haut à droite) |

### En pause

*Clique sur « Reprendre » (ou appuie sur P, Échap ou Entrée) pour continuer, ou sur « Menu » pour revenir au choix du niveau.*

### En fin de partie

*Clique sur « Rejouer » (ou Entrée) pour relancer le même niveau, ou sur « Menu » (ou Échap) pour revenir au choix du niveau.*

## Les niveaux

*Plus le niveau est difficile, plus le plateau est grand, plus le serpent va vite, et plus il est long. Chaque morceau du serpent est une vie : il faut tous les détruire pour gagner.*

| Niveau | Plateau | Vitesse du serpent | Longueur (vies) |
|---|---|---|---|
| Facile | 15 × 15 | lente | 3 |
| Difficile | 20 × 20 | normale | 5 |
| Extrême | 30 × 30 | rapide | 8 |

## Les fruits

*Des fruits sont posés au hasard sur le plateau. Quand le serpent passe dessus, ils lui donnent un effet. Une balle qui touche un fruit le détruit : à toi de choisir lesquels enlever avant que le serpent ne les attrape.*

| Fruit | Effet sur le serpent |
|---|---|
| Bois | C'est un obstacle : le serpent descend d'une ligne et repart dans l'autre sens, il arrive donc plus vite en bas |
| Fraise | Le serpent grandit d'un morceau (une vie de plus) |
| Myrtille | Le serpent devient invincible pendant 5 secondes (il devient cyan et clignote) |
| Pièce d'or | Tous les fruits du plateau sont remplacés par de nouveaux |

## Fin de partie

*Tu gagnes si tu détruis le dernier morceau du serpent. Tu perds si le serpent atteint la ligne du canon.*
