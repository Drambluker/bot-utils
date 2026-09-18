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
Имена Java-модулей — `org.vlaskin.bot.utils` (starter) и
`org.vlaskin.bot.utils.core` (core).

## Использование

Starter настраивает `PebbleEngine` и регистрирует `PebbleService`. Собственный
bean `PebbleService` имеет приоритет над автоконфигурацией.

```java
String message = pebbleService.renderTemplate(
        "messages/welcome", Map.of("user", user));
```

При стандартной конфигурации пример загрузит шаблон
`templates/messages/welcome.peb`.

Начиная с версии `1.1.0` доступны платформенно-независимые компоненты:

- `concurrent.LockByKey<K>`: блокировки по ключу диалога с удалением освобождённых ключей.
  Разделяйте один экземпляр между обработчиками одного диалога; освобождайте
  блокировку в `finally`. `lockInterruptibly` допускает отмену ожидания:
  после InterruptedException блокировку освобождать не нужно. Ключ не может
  быть null; его equals/hashCode должны оставаться неизменными до unlock.
  Блокировки реентерабельны, локальны процессу и не гарантируют справедливость.
- `callback.CallbackPayload` и `CallbackCodec`: код события и параметры,
  разделённые запятыми. Пустые параметры и запятые внутри них запрещены.
  Пробелы вокруг разделителей при чтении удаляются; внутри параметров сохраняются.
  Код события должен быть стабильным и уникальным; для enum используйте `CallbackEvent`,
  а не `ordinal()`. Ограничения размера проверяет адаптер платформы.
- `callback.CallbackParameters`: проверка количества параметров, целых чисел
  и компактных UUID; `callback.CompactUuid` кодирует UUID в 22 URL-safe символа.
  Ошибочный ввод декодеров вызывает IllegalArgumentException; null в encode
  вызывает NullPointerException. Коды могут содержать ASCII-буквы, цифры, `_` и `-`.

Эти компоненты не требуют Spring или API конкретной платформы. Шифрование,
проверку прав и защиту от повторного выполнения кодек не обеспечивает.

## Проверки и релизы

```bash
./mvnw clean verify
```

Процесс публикации, удаления и восстановления пакета описан в
[руководстве по релизам](RELEASING.md).
