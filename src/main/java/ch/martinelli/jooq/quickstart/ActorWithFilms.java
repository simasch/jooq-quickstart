package ch.martinelli.jooq.quickstart;

import java.util.List;

public record ActorWithFilms(String firstName, String lastName, List<FilmName> films) {

    public record FilmName(String name) {
    }
}
