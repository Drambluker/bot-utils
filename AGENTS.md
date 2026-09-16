# Repository Guidelines

## Project Structure & Module Organization

This repository contains a Java 21 Spring Boot starter for rendering bot messages with Pebble. Production code lives in `src/main/java/org/vlaskin/bot/utils`; keep new classes in that package unless a cohesive subpackage is warranted. Spring Boot auto-configuration metadata belongs in `src/main/resources/META-INF/spring`. Tests mirror the production package under `src/test/java`, while test-only Pebble templates live in `src/test/resources/templates`. GitHub Actions workflows are in `.github/workflows`, and release procedures are documented in `RELEASING.md`.

## Build, Test, and Development Commands

Use the checked-in Maven wrapper so local and CI behavior stays consistent. Java 21 and Maven 3.9.5 or newer are enforced.

- `./mvnw test` runs the JUnit test suite.
- `./mvnw clean verify` performs the standard build, generates Javadocs and JaCoCo reports, and enforces coverage.
- `./mvnw -DskipTests package spotbugs:check` builds the JAR and runs static analysis.
- `./mvnw clean verify spotbugs:check cyclonedx:makeBom` reproduces the full pre-release validation.

Build output and reports are written to `target/`; do not commit them.

## Coding Style & Naming Conventions

Follow the existing Java style: four-space indentation, braces on their own lines, explicit imports, and focused `final` classes where extension is not intended. Use `UpperCamelCase` for types, `lowerCamelCase` for methods and variables, and descriptive verb-led method names such as `renderTemplate`. Keep public API Javadocs concise and update auto-configuration imports when adding configuration classes. Preserve the formatting already used in `pom.xml` and workflow YAML.

## Testing Guidelines

Tests use JUnit 5, AssertJ, and Spring Boot's `ApplicationContextRunner`. Name test classes `*Test` and test methods by behavior, for example `rejectsBlankTemplateName`. Add unit tests for validation and rendering logic, plus context-runner tests for auto-configuration changes. `verify` requires at least 90% line coverage and 80% branch coverage.

## Commit & Pull Request Guidelines

Recent commits use a bracketed category followed by an imperative summary, such as `[test] Cover starter configuration` or `[docs] Document starter usage`. Keep each commit focused. Pull requests should explain the behavior change, link relevant issues, note compatibility or configuration impact, and include documentation updates for public API changes. Before requesting review, run `./mvnw clean verify` and `spotbugs:check`; all Verify and Security checks must pass. Screenshots are only needed for changes with visible rendered output.

## Security & Releases

Never commit GitHub tokens or Maven credentials; configure them in `~/.m2/settings.xml`. Follow `RELEASING.md` for versioning and tagged releases. Published versions are immutable, so prefer a new patch release over replacing an artifact.
