# CONTEXT.md — qsurvey-android-lib

> **Contexte projet** : [CONTEXT.md racine](../CONTEXT.md)
> **Documentation locale** : [documentation/README.md](./documentation/README.md) — notes et décisions propres à ce module
> **Documentation technique centrale** : [documents-qmaker-private/](../documents-qmaker-private/)

---

## Identité du module

- **Name:** `qsurvey-android-lib`
- **Package:** `com.android.qmaker.survey.core`
- **Type:** Android Library (JAR), publié comme `com.qmaker:android-qsurvey:1.1.11`
- **Role:** Couche Android du système de sondage. Fournit les composants UI, le worker de push asynchrone, et la persistance SQLite des réponses. S'appuie sur `qsurvey-core-lib` pour les modèles et stratégies de push.
- **Depends on:** `:qsurvey-core-lib`, `:android-processor-tools`, AndroidX

## Build

```bash
./gradlew :qsurvey-android-lib:assembleRelease
./gradlew :qsurvey-android-lib:makeJar
```

- compileSdk 31, minSdk 19, Java (source compatibility par défaut)
- Se compile en JAR (via `release-jar.gradle`)

## Structure du code

| Package | Rôle |
|---------|------|
| `engines/` | `AndroidQSurvey`, `PushWorker`, `UIHandler`, `SQLitePersistenceUnit` |
| `pushers/` | Surcharges Android des pushers HTTP (WSSE, Digest, JWT, Basic) |
| `process/` | `AsyncHttpPushProcess`, `AsycHttpProcess` — push HTTP asynchrone |
| `uis/` | Composants UI de saisie de sondage |
| `utils/displayers/` | `AbstractUIDisplayer`, `NotificationUIDisplayer`, `DialogUIDisplayer` |

### Classes clés

| Classe | Rôle |
|--------|------|
| `AndroidQSurvey` | Implémentation Android de `QSurvey` — point d'entrée pour lancer un sondage depuis une Activity |
| `PushWorker` | Worker background qui exécute les pushers (`android-processor-tools`) |
| `UIHandler` | Bridge entre `PushWorker` et le thread UI |
| `SQLitePersistenceUnit` | Implémentation `PersistenceUnit` via SQLite — stocke les réponses en attente |

### UI Displayers

| Classe | Rôle |
|--------|------|
| `AbstractUIDisplayer` | Base pour l'affichage des états de sondage |
| `NotificationUIDisplayer` | Affiche l'état via une notification Android |
| `DialogUIDisplayer` | Affiche l'état via un Dialog |

### Intégration avec qcmreader

`qcmreader` importe `qsurvey-android-lib` pour afficher et collecter des sondages dans le flux de lecture de QCM. La classe d'intégration côté app est dans `qcmreader/src/.../plugin/survey/`.

---

## Mission de ce module

`qsurvey-android-lib` est le pont Android pour QSurvey. Il ajoute à `qsurvey-core-lib` les workers, displayers UI, persistance SQLite Android et orchestrateurs nécessaires à l'exécution dans une application Android.

---

## Positionnement dans l'écosystème

- Dépend de `:qsurvey-core-lib` et `:android-processor-tools`
- Est intégré notamment dans `qcmreader` pour les usages survey/push
- Toute divergence de comportement entre la couche Android et `qsurvey-core-lib` doit être intentionnelle et documentée

---

## Règles de modification

- Garder ici la **colle Android** : workers, notifications, dialogs, persistance SQLite et intégration cycle de vie
- Ne pas remonter dans ce module des règles de domaine qui appartiennent à `qsurvey-core-lib`
- Éviter de rendre la librairie dépendante de conventions trop spécifiques à `qcmreader` si elles peuvent rester optionnelles
- Les changements de UX sur les displayers doivent rester sobres et ne pas casser les flows silencieux ou en background

---

## Validation minimale

```bash
./gradlew :qsurvey-android-lib:testDebugUnitTest
```

Si le changement touche `PushWorker`, les displayers ou la persistance, vérifier aussi un parcours d'intégration depuis `qcmreader`.

---

## Convention de mise à jour de ce fichier

Mettre à jour quand : contrainte non évidente découverte, nouvelle classe, règle nécessaire en pratique, décision produit.
Protocole de verrou : vérifier `CONTEXT.md.lock`, créer si absent, modifier, supprimer. Utiliser `CONTEXT.md.pending` si lock actif.

---

## Journal des mises à jour

<!-- Ajouter en tête de liste, ne jamais modifier les entrées existantes -->
- [2026-04-22] [Claude] Création initiale — fusion de qsurvey-android-lib/CLAUDE.md + AGENTS.md
