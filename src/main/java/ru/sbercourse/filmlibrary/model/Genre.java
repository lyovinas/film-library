package ru.sbercourse.filmlibrary.model;

public enum Genre {
    FANTASY("Фантастика"),
    COMEDY("Комедия"),
    DRAMA("Драма"),
    ACTION("Боевик");

    private final String genreTextDisplay;

    Genre(String text) {
        genreTextDisplay = text;
    }

    public String getGenreTextDisplay() {
        return genreTextDisplay;
    }
}
