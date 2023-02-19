package ru.yandex.practicum.filmorate.storage.film;

public enum FilmsSortBy {
    YEAR("releaseDate"),
    LIKES("likes");
    private final String fieldName;

    FilmsSortBy(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
