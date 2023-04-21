# java-filmorate
Repository for Filmorate project.

## Sql-диаграмма и пояснения к ней
![SQL](filmorate_sql.png)

### ***film***
Содержит данные о фильме.
Таблица включает такие поля:
* первичный ключ _film_id_ — идентификатор фильма;
* _name_ — название фильма;
* _description_ — описание фильма;
* _release_date_ — дата релиза;
* _duration_ — длительность (в минутах);
* _rating_id_ — идентификатор рейтинга.

### ***film_like***
Содержит данные о лайках, поставленных фильму.
Таблица включает такие поля:
* первичный ключ _user_id_ — идентификатор пользователя, поставившего лайк;
* внешний ключ _film_id_ — идентификатор фильма.

### ***genre***
Содержит информацию о жанрах кино.
В таблицу входят поля:
* первичный ключ _genre_id_ — идентификатор жанра;
* _name_ — название жанра.

### ***film_genre***
Содержит данные о жанрах, указанных фильму.
Таблица включает такие поля:
* первичный ключ _genre_id_ — идентификатор лайка;
* внешний ключ _film_id_ — идентификатор фильма.

### ***rating***
Содержит информацию о рейтинге Ассоциации кинокомпаний.
В таблицу входят поля:
* первичный ключ _rating_id_ — идентификатор рейтинга;
* _name_ — название рейтинга.

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
* внешний ключ _friend_id_ — идентификатор друга пользователя;
* _status_id_ — идентификатор статуса (false — неподтвержденная дружба, true — подтвержденная).

Пример запроса к таблице friendship (вывод всех друзей пользователя с id = 1)
```
SELECT friend_id
FROM friendship
WHERE user_id = 1
GROUP BY user_id
```

При реализации логики приложения необходимо учитывать, что таблица работает в обе стороны.
Если пользователь добавляет друга, дружба становится неподтвержденной.
Чтобы статус дружбы изменился на "подтвержденная", друг взаимно должен добавить пользователя.

### Описание связей между таблицами
#### film.film_id < film_like.film_id > user.user_id
Фильму могут поставить лайки несколько пользователей, один пользователь может поставить несколько лайков разным фильмам.
Реализация связи "многие ко многим" с помощью вспомогательной таблицы film_like.
#### film.film_id < film_genre.film_id > genre.genre_id
Одному фильму соответствует несколько жанров, одному жанру могут соответствовать несколько фильмов.
Реализация связи "многие ко многим" с помощью вспомогательной таблицы film_genre.
#### rating.film_id < film.film_id
Одному рейтингу могут принадлежать несколько фильмов, но у одного фильма не может быть несколько рейтингов.
#### user.user_id < friendship.user_id > user.user_id
У одного пользователя может быть несколько друзей, одна дружба может быть у нескольких пользователей.
Реализация связи "многие ко многим" с помощью вспомогательной таблицы friendship.