package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.core.convert.converter.Converter;

public class StringToFilmsSortByConverter implements Converter<String, FilmsSortBy> {
    private static final String DATA_CONVERSION_ERROR = "Sorting films by field: %s is not supported";

    @Override
    public FilmsSortBy convert(String source) {
        try {
            return FilmsSortBy.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(DATA_CONVERSION_ERROR, source));
        }
    }
}
