DELETE FROM GENRES;
DELETE FROM MPA;
INSERT INTO MPA (MPA_NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO GENRES (GENRE_NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Фантастика'),
       ('Триллер'),
       ('Боевик');