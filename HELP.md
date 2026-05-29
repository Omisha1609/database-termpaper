# AviaCashDesk

Настольное JavaFX-приложение для курсовой работы по базе данных "Авиакасса".

## Что уже есть

- подключение к PostgreSQL через JDBC;
- просмотр пассажиров, рейсов и проданных билетов;
- добавление, редактирование и удаление пассажиров;
- отчеты по объему перевозок и зарплате;
- разделение кода на конфигурацию, подключение к БД, репозитории, модели и UI.

## Подключение к базе

Параметры подключения находятся в `src/main/resources/application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/avia_coursework
db.user=mihailorlov
db.password=
```

Если пароль у пользователя PostgreSQL есть, впиши его в `db.password`.

Также можно переопределить параметры через переменные окружения:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

## Запуск

```bash
./gradlew run
```

В IntelliJ IDEA можно запустить класс:

```text
org.example.databasetermpaper.AppLauncher
```

Класс `AviaCashDeskApp` наследуется от JavaFX `Application`, поэтому в IntelliJ его лучше не запускать напрямую. `AppLauncher` нужен именно для корректного старта JavaFX из IDE.

## Проверка сборки

```bash
./gradlew clean test
```

## Создание базы из скриптов

Если базу нужно пересоздать:

```bash
dropdb --if-exists avia_coursework
createdb avia_coursework
psql -d avia_coursework -f database/01_schema.sql
psql -d avia_coursework -f database/02_seed.sql
```

## Как описать в курсовой

Приложение построено по многослойной архитектуре:

- `ui` - графический интерфейс JavaFX;
- `repository` - SQL-запросы и операции с таблицами;
- `db` - подключение к PostgreSQL;
- `model` - классы предметной области;
- `config` - чтение настроек приложения.

Клиентская часть отвечает за ввод данных, отображение таблиц и запуск отчетов. Серверная часть представлена СУБД PostgreSQL, где выполняются ограничения целостности, связи таблиц и аналитические SQL-запросы.
