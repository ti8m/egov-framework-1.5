# eGovFramework

📖 Dokumentation: https://ti8m.github.io/egov-framework-1.5/index.html

eGovFramework ist ti8m's Spring Boot Framework für Schweizer E-Government-Anwendungen. Es ist ein Multi-Modul
Maven-Reactor (Java 21, Spring Boot 3.5.14).

## Module

Der Root-`pom.xml` aggregiert die Module in Build-Reihenfolge; nachgelagerte Module hängen von vorgelagerten ab:

1. **e-gov-bom** — reine Dependency-Management-BOM; zentralisiert die Versionsabstimmung für Konsumenten und alle
   Geschwistermodule. Enthält keinen Quellcode — hier wird beim Anheben einer gemeinsamen Abhängigkeitsversion
   angepasst.
2. **e-gov-framework** — die Kernbibliothek. Alle anderen Module hängen davon ab. Wichtige Packages unter
   `ch.ti8m.egov.framework`:
   - `iam` — Rollen-/Berechtigungs- und regelbasierte Validierungs-("Ruleset")-Engine: Annotationen (`@Ruleset`,
     `@Validation`, `@AllowedForRoles`, `@AllowedForStates`) in `iam.generation`, JPA-Entities in
     `iam.persistence.model.ruleset`/`.role`/`.permission`, Services in `iam.components`, öffentliche
     Interfaces in `iam.api.java`, REST in `iam.api.rest`.
   - `persistence` — Basis-Repository-Abstraktion, generische Filter-/Query-Parsing-Engine, berechtigungsbewusste
     Queries.
   - `validation` — das Command-Ausführungsframework: `validation.command` (Actions, Executors, Handler,
     Proxies) und `validation.engine` (Auswertung von Regelausdrücken, z. B.
     `['MIN_MAX_LENGTH', 'root.title', '3', '50']`).
   - `exceptionhandling`, `documentation`, `deployconfig`, `rest` — Querschnitts-Infrastruktur.
3. **e-gov-ai-rule-generation** — hängt von e-gov-framework + Spring AI (Azure OpenAI) ab. Generiert/modifiziert
   IAM-Ruleset-Definitionen mittels LLM. Packages: `airulegeneration.api`, `config`, `generator`, `modification`.
4. **e-gov-mdm** — Stammdatenverwaltung (Master Data Management), Domäne und JPA-Persistenz
   (`mdm.persistence.*`, `mdm.api.abstraction/direct/types/user`).
5. **e-gov-mdm-api** — REST-Schicht auf Basis von e-gov-mdm (`controller`, `dto`, `mapper` (MapStruct),
   `service`, `validation`). Führt das `framework-ruleset-generator` Maven-Plugin während `generate-resources`
   aus.
6. **e-gov-framework-demo** — eine lauffähige Spring Boot Beispielanwendung (`mainClass ch.ti8m.egov.demo.Main`),
   die einen vollständigen vertikalen Durchstich (Entity → Repository → Command/Executor → Validation →
   REST-Controller) auf Basis des Frameworks zeigt. Die "Gescheaft"-Domänenklassen sind das Referenzbeispiel für
   die Nutzung von e-gov-framework.
7. **e-gov-luid** — ein weitgehend unabhängiges Modul (hängt nur von Standard-Spring-Boot-Startern ab, nicht von
   e-gov-framework), das lokal eindeutige IDs mit einem Heartbeat-Mechanismus generiert.

## Build und Tests

Verwende den Maven-Wrapper (`./mvnw` / `mvnw.cmd`) im Root-Verzeichnis — er baut alle Module in Abhängigkeits-
reihenfolge.

- Alles bauen: `./mvnw clean install`
- Bauen ohne Tests: `./mvnw clean install -DskipTests`
- Alle Tests eines Moduls ausführen: `./mvnw -pl e-gov-framework test`
- Einzelne Testklasse ausführen: `./mvnw -pl e-gov-framework test -Dtest=BaseRepositoryImplIT`
- Einzelne Testmethode ausführen: `./mvnw -pl e-gov-framework test -Dtest=BaseRepositoryImplIT#someMethod`

Integrationstests heissen `*IT.java` und starten reale Abhängigkeiten via **Testcontainers** (Postgres, SQL
Server, Zookeeper) statt über das Root-`docker-compose.yml` (dieses ist für manuelle lokale Läufe gedacht, nicht
für die Testsuite). Flyway verwaltet die Test-Schema-Migrationen.
