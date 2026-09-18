# Repository Guidelines

## Project Structure & Module Organization

This Java 21 Maven reactor contains `bot-utils-core` (callback codecs and keyed locks, no runtime dependencies) and `bot-utils-starter` (Spring Boot and Pebble). Sources and tests use `<module>/src/main/java` and `<module>/src/test/java`. Callback codecs belong in `org.vlaskin.bot.utils.callback`, keyed locks in `org.vlaskin.bot.utils.concurrent`, and rendering services in `org.vlaskin.bot.utils`. Keep shared utilities independent of Telegram, MAX, and application entities. Auto-configuration metadata and test templates belong in the starter's resources. CI workflows are in `.github/workflows`; release procedures are documented in `RELEASING.md`.

## Build, Test, and Development Commands

Use the checked-in Maven wrapper so local and CI behavior stays consistent. Java 21 and Maven 3.9.5 or newer are enforced.

- `./mvnw test` runs the JUnit test suite.
- `./mvnw clean verify` performs the standard build, generates Javadocs and JaCoCo reports, and enforces coverage.
- `./mvnw -DskipTests package spotbugs:check` builds the JAR and runs static analysis.
- `./mvnw clean verify spotbugs:check cyclonedx:makeBom` reproduces the full pre-release validation.

Build output and reports are written to each module's `target/`; the aggregate SBOM is at `target/classes/META-INF/sbom/application.cdx.json`. Do not commit generated output.

## Coding Style & Naming Conventions

Follow the existing Java style: four-space indentation, braces on their own lines, explicit imports, and focused `final` classes where extension is not intended. Use `UpperCamelCase` for types, `lowerCamelCase` for methods and variables, and descriptive verb-led method names such as `renderTemplate`. Keep public API Javadocs concise and update auto-configuration imports when adding configuration classes. Preserve the formatting already used in `pom.xml` and workflow YAML.

## Testing Guidelines

Tests use JUnit 5 and AssertJ; only the starter uses Spring Boot's `ApplicationContextRunner`. Name test classes `*Test` and methods by behavior. Concurrent tests must release held locks before waiting for workers and use bounded termination. Add tests for validation, interruption, rendering, and auto-configuration. `verify` requires at least 90% line and 80% branch coverage in each module.

## Commit & Pull Request Guidelines

Recent commits use a bracketed category followed by an imperative summary, such as `[test] Cover starter configuration` or `[docs] Document starter usage`. Keep each commit focused. Pull requests should explain the behavior change, link relevant issues, note compatibility or configuration impact, and include documentation updates for public API changes. Before requesting review, run `./mvnw clean verify` and `spotbugs:check`; all Verify and Security checks must pass. Screenshots are only needed for changes with visible rendered output.

## Security & Releases

Never commit GitHub tokens or Maven credentials; configure them in `~/.m2/settings.xml`. Follow `RELEASING.md` for versioning and tagged releases. Published versions are immutable, so prefer a new patch release over replacing an artifact.
