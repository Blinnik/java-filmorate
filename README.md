# java-filmorate
Repository for Filmorate project.

## Sql-диаграмма и пояснения к ней
![SQL](https://raw.githubusercontent.com/Blinnik/java-filmorate/e3e9f65fad6310c97c479a30c265b6ca35550098/filmorate_sql.png)

### ***film***
Содержит данные о фильме.
Таблица включает такие поля:
* первичный ключ _film_id_ — идентификатор фильма;
* _name_ — название фильма;
* _description_ — описание фильма;
* _release_date_ — дата релиза;
* _duration_ — длительность (в минутах).

### ***film_like***
Содержит данные о лайках, поставленных фильму.
Таблица включает такие поля:
* первичный ключ _like_id_ — идентификатор лайка;
* _film_id_ — идентификатор фильма.

### ***genre***
Содержит информацию о жанрах кино.
В таблицу входят поля:
* первичный ключ _genre_id_ — идентификатор жанра;
* _name_ — название жанра.

### ***film_genre***
Содержит данные о жанрах, указанных фильму.
Таблица включает такие поля:
* первичный ключ _genre_id_ — идентификатор лайка;
* _film_id_ — идентификатор фильма.

### ***rating***
Содержит информацию о рейтинге Ассоциации кинокомпаний.
В таблицу входят поля:
* первичный ключ _rating_id_ — идентификатор рейтинга;
* _name_ — название рейтинга.

### ***film_rating***
Содержит данные о рейтинге, которому соответствует фильм.
Таблица включает такие поля:
* первичный ключ _rating_id_ — идентификатор рейтинга;
* _film_id_ — идентификатор фильма.

### ***user***
Содержит данные о пользователе.
Таблица включает такие поля:
* первичный ключ _user_id_ — идентификатор пользователя;
* _email_ — электронная почта пользователя;
* _login_ — логин пользователя;
* _name_ — имя пользователя;
* _birthday_ — дата рождения.

### ***friendship***
Содержит информацию о дружбе между двумя пользователями.
В таблицу входят поля:
* первичный ключ _user_id_ — идентификатор пользователя;
* _friend_id_ — идентификатор друга пользователя;
* _status_id_ — идентификатор статуса.

Пример запроса к таблице friendship (вывод всех друзей пользователя с id = 1)
```
SELECT friend_id
FROM friendship
WHERE user_id = 1
GROUP BY user_id
```

При реализации логики приложения необходимо учитывать, что таблица работает в обе стороны.
Если пользователь добавляет друга, дружба становится неподтвержденной.
Чтобы статус дружбы изменился на "подтвержденная", друг взаимно должен добавить пользователя

### ***friendship_status***
Содержит данные о статусе дружбы.
Таблица включает такие поля:
* первичный ключ _status_id_ — идентификатор статуса;
* _status_ — статус (подтвержденная/неподтвержденная дружба).