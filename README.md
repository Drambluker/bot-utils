# bot-utils

Spring Boot starter с общими компонентами для ботов любого типа, не
привязанный к платформе или каналу доставки. Проект распространяется по
лицензии [Apache License 2.0](LICENSE); сведения об авторстве приведены в
[NOTICE](NOTICE).

## Подключение

Для версии `1.x` требуются Java 21 и Spring Boot 4.1.x. Добавьте GitHub
Packages и starter:

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

Для загрузки пакета настройте сервер `github` в `~/.m2/settings.xml`, используя
имя пользователя GitHub и PAT с правом `read:packages`. Не сохраняйте PAT в
проекте. Стабильное имя Java-модуля — `org.vlaskin.bot.utils`.

## Использование

Starter настраивает `PebbleEngine` и регистрирует `PebbleService`. Собственный
bean `PebbleService` имеет приоритет над автоконфигурацией.

```java
String message = pebbleService.renderTemplate(
        "messages/welcome", Map.of("user", user));
```

При стандартной конфигурации пример загрузит шаблон
`templates/messages/welcome.peb`.

## Проверки и релизы

```bash
./mvnw clean verify
```

Процесс публикации, удаления и восстановления пакета описан в
[руководстве по релизам](RELEASING.md).
