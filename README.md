# bot-utils

Spring Boot starter с общими компонентами для ботов любого типа, не привязанный
к конкретной платформе или каналу доставки.

Проект распространяется по лицензии [Apache License 2.0](LICENSE). Сведения об
авторстве, которые необходимо сохранять при распространении, приведены в
[NOTICE](NOTICE).

## Подключение

Для версии `1.x` требуются Java 21 или новее и Spring Boot 4.1.x. Поддержка
других мажорных и минорных линий Spring Boot отдельно не гарантируется.
Стабильное имя модуля для module path — `org.vlaskin.bot.utils`.

Добавьте GitHub Packages как Maven-репозиторий и starter как зависимость:

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/Drambluker/bot-utils</url>
</repository>

<dependency>
  <groupId>org.vlaskin.bot</groupId>
  <artifactId>bot-utils-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

Для чтения GitHub Packages настройте сервер `github` в `~/.m2/settings.xml`,
используя имя пользователя GitHub и PAT с правом `read:packages`. Не сохраняйте
PAT в POM или репозитории.

Pebble starter создаёт `PebbleEngine`, после чего `bot-utils` автоматически
регистрирует `PebbleService`. Имя передаётся загрузчику Pebble, поэтому для
шаблона `templates/messages/welcome.peb` при стандартном префиксе используйте:

```java
String message = pebbleService.renderTemplate(
        "messages/welcome", Map.of("user", user));
```

Собственный bean `PebbleService` имеет приоритет над автоконфигурацией starter.

Релизы публикуются в GitHub Packages с координатами
`org.vlaskin.bot:bot-utils-starter`. Workflow `Publish Maven package`
запускается тегом, совпадающим с версией из `pom.xml`, например `v1.0.0`.
Полный процесс выпуска, удаления и аварийной замены версии описан в
[руководстве](RELEASING.md).

## Проверки

`./mvnw clean verify` запускает тесты, формирует JaCoCo-отчёт и проверяет порог
покрытия. `./mvnw -DskipTests package spotbugs:check` запускает статический
анализ, а `./mvnw -DskipTests cyclonedx:makeBom` создаёт SBOM для Trivy.
Workflow `Security` дополнительно сканирует историю через Gitleaks и зависимости
через Trivy.
