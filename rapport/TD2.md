# TD2

## Git Trunk (ou Trunk-Based Development)

Définition :

Git Trunk est une méthode de gestion des branches dans Git où tous les développeurs travaillent directement (ou presque) sur une branche principale (souvent appelée main ou trunk). Les changements sont fréquemment intégrés dans cette branche principale via des commits réguliers et des intégrations continues.

Caractéristiques :

	1.	Branche unique et stable : La branche principale (trunk) reste au cœur des développements et est constamment mise à jour.
	2.	Changements fréquents : Les développeurs poussent régulièrement des petites modifications (souvent après validation via des revues de code).
	3.	Branches courtes : Si des branches sont créées, elles sont temporaires (par exemple, pour une fonctionnalité ou un correctif) et fusionnées rapidement.
	4.	Intégration continue : Git Trunk est souvent associé à des pratiques d’intégration continue (CI), où les changements sont automatiquement testés et intégrés.

Utiliser les features-flags pour limiter la sortie des nouvelles fonctionnalités à un sous-ensemble d'utilisateurs.

Il faut que l'équipe connaisse très bien son sujet car nécessite une organisation très rigoureuse.


## GitFlow

Définition :

GitFlow est un modèle de gestion des branches dans Git qui définit un workflow précis pour les branches et les versions.
Chaque feature a sa branche dédiée, qui est fusionnée dans la branche de développement une fois terminée.

### Avantages

- Gestions des versions simplifiée
- Facilite la collaboration
- Permet de travailler sur plusieurs versions en parallèle

### Inconvénients

- Plus grande compléxité
- Trop de branche à gérer
- Pas adapté pour les petits projets


## Git Trunk


1. Définissez le feature-flags (aussi appelé feature-toggles)

Les feature-flags est une technique de développement permettant de contrôler la sortie de fonctionnalités
dans une application. Cela permet de limiter la sortie des nouvelles fonctionnalités à un sous-ensemble d'utilisateurs.
L'objectif est de pouvoir activer ou désactiver une fonctionnalité à tout moment, sans avoir à déployer une nouvelle version de l'application.

2. Indiquez les moyens usuels d’implémenter du feature-flags

Il existe plusieurs moyens d'implémenter des feature-flags dans une application :

- Les variables d'environnement : Les feature-flags peuvent être implémentés en utilisant des variables d'environnement.
- Un fichier JSON : Les feature-flags peuvent être stockés dans un fichier JSON.
- Une base de données : Les feature-flags peuvent être stockés dans une base de données. (Gestionnaire de resources Apache ZooKeeper, Redis, etc.)

Important de garder une solution scalable et facile à maintenir.


3. Décrire le flux de travail du Trunk-Based Repository

Push des features en Squash pour garder un historique propre.
Le flux de travail du Trunk-Based Repository est le suivant :

1. Les développeurs travaillent directement sur la branche principale (trunk).
2. Les changements sont fréquemment intégrés dans la branche principale via des commits réguliers et des intégrations continues.
3. Les développeurs poussent régulièrement des petites modifications (souvent après validation via des revues de code).
4. Si des branches sont créées, elles sont temporaires (par exemple, pour une fonctionnalité ou un correctif) et fusionnées rapidement.

Quand une release est prête on crée une branche release qui sert de support pour les correctifs de bugs.


## GitFlow

1. Décrire le flux de travail du GitFlow

Le flux de travail du GitFlow est le suivant :

1. La branche principale est la branche master, qui contient le code de production.
2. La branche de développement est la branche develop, qui contient le code en cours de développement.
3. Les fonctionnalités sont développées dans des branches feature, qui sont fusionnées dans la branche develop une fois terminées.
4. Les correctifs de bugs sont développés dans des branches hotfix, qui sont fusionnées dans la branche master et develop.
5. Les releases sont développées dans des branches release, qui sont fusionnées dans la branche master et develop.
6. Les versions sont taguées dans la branche master.
7. Les branches feature, hotfix et release sont supprimées une fois fusionnées.
8. Les branches master et develop sont maintenues en permanence.

2. Décrire la méthode préférée pour gérer plusieurs versions majeures/mineures en
parallèle

On crée une branche support tirée de la branche release de la version en question

En cas de reglement de bug sur la branche support on merge sur le develop et toutes les branches de support ultérieures.


## Donnez les noms de branches correspondant aux situations suivantes :
- Une fonctionnalité « Gestion des utilisateurs – suppression » (ticket n°B-768) :

> `feature/B-768-user-management-deletion`

- Un fix « Mauvaise redirection après ajout d’un email à l’utilisateur » (ticket A-46) :

> `hotfix/A-46-bad-redirection-after-adding-email-to-user`

- L’ajout d’une configuration « devcontainer » pour l’environnement de développement :

> `feature/devcontainer-configuration` ou commit directement sur develop

- Un hotfix pour préparer un patch depuis une version 1.3.1 :

> `hotfix/1.3.1-patch-preparation`
- Une release mineure après 1.4.17 :

> `release/1.5.0`

- Une branche support après release 12.5.6 :

> `support/12.5.X`



## Commit messages

- scope: subject (optionnel)
- body (optionnel)


type de commit:

- Deprecate
- build
- style
- refactor
- feat
- docs
- fix
- chore
- perf (performance)
- test (tests)
- ci (changement des fichiers de configuration ou des scripts de CI)


Un BREAKING CHANGE implique un changement majeur qui peut casser la compatibilité avec les versions précédentes.
Ce changement peut aussi déprécié des interfaces ou des fonctionnalités.


## Semantic versioning 

### RC.2

•	1 : Numéro de version majeure
	•	Il s’agit de la première version majeure du logiciel.
	•	0 : Numéro de version mineure
	•	Aucun ajout significatif de nouvelles fonctionnalités depuis la version majeure.
	•	0 : Numéro de patch
	•	Aucun correctif mineur ou bugfix n’a été appliqué.
	•	RC.2 : Release Candidate (version candidate à la sortie)
	•	Le suffixe RC indique que cette version est en phase finale de test avant d’être déclarée stable.
	•	Le numéro 2 signifie qu’il s’agit de la deuxième itération de cette version candidate.

Conclusion :
Version 1.0.0-RC.2 est une version de pré-production, proche de la sortie officielle (1.0.0 stable), et représente une étape avancée dans les tests.


### 1.0.0-snapshot+build.9cbd45f6

- 1 : Numéro de version majeure
  - Il s’agit de la première version majeure du logiciel.
  - 0 : Numéro de version mineure
  - Aucun ajout significatif de nouvelles fonctionnalités depuis la version majeure.
  - 0 : Numéro de patch
  - Aucun correctif mineur ou bugfix n’a été appliqué.
  - snapshot : Version de développement
  - Le suffixe snapshot indique que cette version est en cours de développement et n’est pas encore stable.
  - build.9cbd45f6 : Build ID

Majeur suivante : 1.0.0
PreMajeur suivante

Pre mineur suivante : 1.0.0