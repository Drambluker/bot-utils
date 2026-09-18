# Выпуск Maven-пакета

Parent POM, core и starter публикуются в GitHub Packages одним Maven reactor
автоматически по тегу `v<version>`.
Версия в теге должна точно совпадать с `project.version` в `pom.xml`.

## Подготовка релиза

Используйте [Semantic Versioning](https://semver.org/lang/ru/): исправление без
изменения API повышает PATCH, обратно совместимая возможность — MINOR, ломающее
изменение — MAJOR.

1. Обновите `<version>` в корневом `pom.xml` и версии `<parent>` в
   `bot-utils-core/pom.xml` и `bot-utils-starter/pom.xml` одним номером.
   Релизная версия не должна содержать `-SNAPSHOT`.
2. Обновите примеры версии в документации. В `CHANGELOG.md` перенесите
   изменения из раздела «Следующая версия» в раздел с номером релиза.
3. Запустите `./mvnw clean verify spotbugs:check cyclonedx:makeBom`.
4. Создайте отдельный коммит, например
   `git commit -am "[release] Prepare 1.1.0"`, и отправьте его в `main`.
5. Дождитесь успешного завершения обязательных проверок.
6. Создайте аннотированный тег на проверенном коммите и отправьте его:

   ```bash
   git switch main
   git pull --ff-only
   git tag -a v1.1.0 -m 'Release 1.1.0'
   git push origin v1.1.0
   ```

Workflow `Publish Maven package` проверит историю, release-коммит, тесты,
SpotBugs и агрегированный SBOM, затем опубликует parent POM и для каждого
модуля JAR, исходники, JavaDoc и SBOM.
Обновляйте потребителей только после появления версии в разделе `Packages`.

Опубликованная версия неизменяема. Для следующего изменения назначьте новый
номер; повторный запуск workflow с существующим номером должен завершиться
ошибкой.

## Gitleaks и Trivy локально

```bash
docker run --rm -v "$PWD:/repo:ro" \
  ghcr.io/gitleaks/gitleaks:v8.30.1 \
  detect --source /repo --redact --no-banner

./mvnw -DskipTests package
docker run --rm -v "$PWD:/repo:ro" \
  aquasec/trivy:0.74.0 \
  sbom --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 \
  /repo/target/classes/META-INF/sbom/application.cdx.json
```

## Обязательная настройка GitHub

После загрузки workflow откройте `Settings → Rules → Rulesets` и примените к
ветке `main` правило, запрещающее force push. При работе через pull request
добавьте в `Require status checks to pass` проверки:

- `Verify / test`;
- `Security / Secret history`;
- `Security / Java and dependencies`.

В `Settings → Code security` включите Dependabot alerts, Dependabot security
updates, Secret scanning и Push protection. Для релизных тегов `v*` создайте
отдельное правило, запрещающее их изменение и удаление.

## Удаление и восстановление

> **Только для крайнего случая.** Удаление публичной версии может сломать чужие
> сборки. Почти всегда правильнее выпустить новую PATCH-версию.

Считайте parent, core и starter одним релизным набором: удаление parent или
core ломает загрузку starter. При восстановлении возвращайте весь набор.
Версию можно удалить через страницу пакета: `Packages` → пакет → версия →
`Delete version`. Перед удалением запишите Maven-координаты, номер версии,
commit SHA и tag SHA.

Удалённую версию можно восстановить в течение 30 дней, если её пространство
имён ещё свободно. Необходимые права токена и REST-запросы приведены в
[документации GitHub](https://docs.github.com/en/packages/learn-github-packages/deleting-and-restoring-a-package).

## Аварийная перепубликация того же номера

> **Только если выпуск новой версии технически невозможен.** Разные Maven-кэши
> могут сохранить разные JAR с одинаковыми координатами, поэтому результат
> перестанет быть воспроизводимым.

1. Остановите сборки потребителей и сохраните копию JAR, POM, контрольных сумм,
   старого commit SHA и tag SHA.
2. Удалите версию пакета и убедитесь, что она исчезла из GitHub Packages.
3. Получите временный bypass защищающего `v*` ruleset либо временно отключите
   правило. Удалите тег, исправьте код и создайте одноимённый тег на новом
   проверенном release-коммите:

   ```bash
   git push origin :refs/tags/v1.1.0
   git tag --delete v1.1.0
   git tag -a v1.1.0 -m 'Emergency replacement of 1.1.0'
   git push origin v1.1.0
   ```

4. Сразу восстановите защиту тегов и убедитесь, что ruleset снова активен.
5. Дождитесь повторной публикации. Очистите эту зависимость из локальных
   Maven-кэшей и кэшей GitHub Actions всех потребителей, затем полностью
   пересоберите их и сравните контрольные суммы загруженного JAR.
6. Зафиксируйте причину, старый и новый commit SHA и затронутые сборки.

Если перепубликация не состоялась, восстановите удалённую версию в пределах
30 дней либо выпустите версию с новым номером.
