# bot-utils

Общие компоненты для ботов любого типа, не привязанные к платформе или
каналу доставки. Проект распространяется по
лицензии [Apache License 2.0](LICENSE); сведения об авторстве приведены в
[NOTICE](NOTICE).

## Подключение

Требуется Java 21. `bot-utils-core` не имеет runtime-зависимостей;
`bot-utils-starter` подключает core и Pebble для Spring Boot 4.1.x.
Добавьте GitHub Packages и нужный артефакт:

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/Drambluker/bot-utils</url>
</repository>

<dependency>
  <groupId>org.vlaskin.bot</groupId>
  <artifactId>bot-utils-starter</artifactId>
  <version>1.1.0</version>
</dependency>
```

Для загрузки пакета настройте сервер `github` в `~/.m2/settings.xml`, используя
имя пользователя GitHub и PAT с правом `read:packages`. Не сохраняйте PAT в
проекте. Для приложения без Spring замените `artifactId` на `bot-utils-core`.

## Использование

Starter настраивает `PebbleEngine` и регистрирует `PebbleService`. Собственный
bean `PebbleService` имеет приоритет над автоконфигурацией.

```java
String message = pebbleService.renderTemplate(
        "messages/welcome", Map.of("user", user));
```

При стандартной конфигурации пример загрузит шаблон
`templates/messages/welcome.peb`.

Core предоставляет блокировки по ключу, callback-кодек и компактное
представление UUID. Контракты и ограничения описаны в [справочнике](USAGE.md).

## Проверки и релизы

```bash
./mvnw clean verify
```

- [История изменений](CHANGELOG.md).
- [Публикация, удаление и восстановление пакетов](RELEASING.md).
