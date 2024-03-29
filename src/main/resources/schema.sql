DROP ALL OBJECTS DELETE FILES;


CREATE TABLE IF NOT EXISTS rating (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS film (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR(200) NOT NULL,
    releaseDate DATE NOT NULL,
    duration INTEGER NOT NULL,
    rating_id INTEGER NOT NULL,
    FOREIGN KEY (rating_id) REFERENCES rating(rating_id) ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS film_genres (
    genre_record_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS user_list (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    birthday DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS film_likes (
    like_record_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_list(user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS friendship_status (
    status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status_name VARCHAR NOT NULL
);
CREATE TABLE IF NOT EXISTS friends_list (
    friend_record_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    accepter_id INTEGER NOT NULL,
    status_id INTEGER NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES user_list(user_id) ON DELETE CASCADE,
    FOREIGN KEY (accepter_id) REFERENCES user_list(user_id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES friendship_status(status_id) ON DELETE RESTRICT
);