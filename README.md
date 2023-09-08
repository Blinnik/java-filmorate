# Filmorate
A social network where you can choose a movie based on what ratings you and your friends give to movies.

## Technology stack

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

## Functionality
+ creating a user, updating a user, getting a list of all users;
+ adding friends, removing from friends, displaying a list of mutual friends;
+ adding a movie, updating a movie, getting all movies, searching for movies;
+ adding and removing likes, displaying most popular movies by the number of likes;
+ manipulations with genres;
+ manipulations with MPA.

## Setup
1. [Install Java 11 JDK](https://hg.openjdk.org/jdk/jdk11)
2. Clone this repository to your local machine
```shell
git clone git@github.com:Blinnik/java-filmorate.git
```
3. Run application

## Group project
The project was finalized during teamwork in another [repository](https://github.com/LLaym/filmorate).
I was developing the "reviews" and "event feed" functionality, in addition, I was reworking the project structure, validation

## SQL Diagram
![SQL](filmorate_sql.png)

### ***films***
Contains data about the movie.
The table includes the following fields:
* primary key _film_id_ — movie ID;
* _name_ — the name of the movie;
* _description_ — description of the movie;
* _release_date_ — release date;
* _duration_ — duration (in minutes);
* _mpa_id_ — rating ID.

### ***films_likes***
Contains data about likes assigned to the movie.
The table includes the following fields:
* primary key _user_id_ — ID of the user who put the like;
* the foreign key _film_id_ — ID of the movie.

### ***genres***
Contains information about movie genres.
The table includes the following fields:
* primary key _genre_id_ — genre ID;
* _name_ — the name of the genre.

### ***films_genres***
Contains data about the genres specified for the film.
The table includes the following fields:
* primary key _genre_id_ — like ID;
* the foreign key _film_id_ — ID of the movie.

### ***mpas***
Contains information about the rating of the Association of Film Companies.
The table includes the following fields:
* primary key _mpa_id_ — rating ID;
* _name_ — name of the rating.

### ***users***
Contains data about the user.
The table includes the following fields:
* primary key _user_id_ — user ID;
* _email_ — user's email address;
* _login_ — user login;
* _name_ — username;
* _birthday_ — date of birth.

### ***friendships***
Contains information about the friendship between two users.
The table includes the following fields:
* primary key _user_id_ — user ID;
* the foreign key _friend_id_ — user's friend ID;
* _status_ — status (false — unconfirmed friendship, true — confirmed).

#### The work of the business logic associated with the table
User 1 adds user 2 as a friend.
An entry appears in the table of the form {"user_id" = 1, "friend_id" = 2, "status" = false}.
User 2 has user 1 as a friend, but not vice versa.
User 2 needs to add user 1 to friends,
then the initial record will change and look like this: {"user_id" = 1, "friend_id" = 2, "status" = true}.
A new record is not created in this case. Now user 1 has user 2 as a friend

#### Request example
Example of a query to the friendships table (output of all the user's friends with id = 1)
```
SELECT friend_id 
FROM friendships 
WHERE user_id = 1 AND status = true
UNION 
SELECT user_id AS friend_id 
FROM friendships 
WHERE friend_id = 1
```
### Description of relationships between tables
#### films.film_id < films_likes.film_id > users.user_id
A movie can be liked by several users, one user can put several links to different movies.
Implementation of the many-to-many relationship using the films_likes auxiliary table.
#### films.film_id < films_genres.film_id > genres.genre_id
Several genres correspond to one film, several films can correspond to one genre.
Implementation of the many-to-many relationship using the films_genres auxiliary table.
#### mpas.film_id < films.film_id
Several films can belong to one rating, but one film cannot have several ratings.
#### users.user_id < friendships.user_id > users.user_id
One user can have several friends, one friendship can have several users.
Implementation of the many-to-many relationship using the friendships auxiliary table.