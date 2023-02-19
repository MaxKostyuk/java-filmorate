DROP ALL OBJECTS DELETE FILES;

CREATE TABLE IF NOT EXISTS user_events (
   event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   user_id INTEGER NOT NULL,
   event_type VARCHAR(20) NOT NULL,
   operation VARCHAR(10) NOT NULL,
   entity_id INTEGER NOT NULL,
   timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS rating (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS director (
    director_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
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
CREATE TABLE IF NOT EXISTS film_directors (
    film_id INTEGER,
    director_id INTEGER,
    CONSTRAINT film_directors_to_film_fk
    FOREIGN KEY(film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    CONSTRAINT film_directors_to_director_fk
    FOREIGN KEY(director_id) REFERENCES director(director_id) ON DELETE CASCADE,
    PRIMARY KEY(film_id, director_id)
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
CREATE TABLE IF NOT EXISTS review (
    reviewId INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR NOT NULL,
    isPositive BOOLEAN NOT NULL,
    userId INTEGER NOT NULL,
    filmId INTEGER NOT NULL,
    FOREIGN KEY (userId) REFERENCES user_list(user_id) ON DELETE CASCADE,
    FOREIGN KEY (filmId) REFERENCES film(film_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS review_likes (
    like_record_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    reviewId INTEGER NOT NULL,
    userId INTEGER NOT NULL,
    FOREIGN KEY (reviewId) REFERENCES review(reviewId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user_list(user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS review_dislikes (
    dislike_record_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    reviewId INTEGER NOT NULL,
    userId INTEGER NOT NULL,
    FOREIGN KEY (reviewId) REFERENCES review(reviewId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user_list(user_id) ON DELETE CASCADE
);
