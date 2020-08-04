# Global-Whitelist-Waterfall

**Global-Whitelist-Waterfall** est un plugin de whitelist pour votre serveur [Waterfall](https://papermc.io/)

## Installation

> Notez que vous avez besoin d'une base de donnée MySQL pour que ce plugin fonctionne

1. Aller sur la page [_lastest release_](https://github.com/Samuel-Martineau/Global-Whitelist-Waterfall/releases/latest/) et télécharcher le fichier `.jar` sur le serveur
2. Déplacer ce fichier dans le dossier `plugins`
3. Redémarrer le serveur afin que le plugin puisse générer sa configuration _(Le plugin se désactivera après ceci)_
4. Dans le fichier `config.yml` du plugin, remplir les champs suivants:

```yml
db:
  name: 'NOM DE LA BASE DE DONNÉE'
  table: 'NOM DE LA TABLE'
  user: 'NOM D'UTILISATEUR'
  password: 'MOT DE PASSE'
```

5. Redémarrer le serveur

## Utilisation

Sur votre serveur, vous pouvez désormais utiliser les deux commandes suivantes:

- `/gwhitelist add <player name>` _(Ajoute un joueur à la liste blanche)_
- `/gwhitelist remove <player name>` _(Supprime un joueur de la liste blanche)_

## Auteurs

- **[Samuel Martineau](https://github.com/Samuel-Martineau/)**
- **[Vu Dang Khoa Chiem](https://github.com/Doudou8)**
