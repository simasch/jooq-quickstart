package ch.martinelli.jooq.quickstart;

import java.util.List;

public record ActorWithFilms(String firstName, String lastName, List<FilmWithName> films) {

    public record FilmWithName(String name) {
    }
}
