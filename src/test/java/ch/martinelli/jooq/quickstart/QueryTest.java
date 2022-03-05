package ch.martinelli.jooq.quickstart;

import ch.martinelli.jooq.quickstart.database.tables.Actor;
import ch.martinelli.jooq.quickstart.database.tables.Category;
import ch.martinelli.jooq.quickstart.database.tables.Film;
import ch.martinelli.jooq.quickstart.database.tables.FilmActor;
import ch.martinelli.jooq.quickstart.database.tables.FilmCategory;
import ch.martinelli.jooq.quickstart.database.tables.records.FilmRecord;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Record2;
import org.jooq.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static ch.martinelli.jooq.quickstart.database.tables.Actor.ACTOR;
import static ch.martinelli.jooq.quickstart.database.tables.Category.CATEGORY;
import static ch.martinelli.jooq.quickstart.database.tables.Film.FILM;
import static ch.martinelli.jooq.quickstart.database.tables.FilmActor.FILM_ACTOR;
import static ch.martinelli.jooq.quickstart.database.tables.FilmCategory.FILM_CATEGORY;

@Transactional
@SpringBootTest
class QueryTest {

    @Autowired
    private DSLContext dsl;

    @Test
    void find_all_films() {
        Result<FilmRecord> films = dsl.selectFrom(FILM).fetch();

        Assertions.assertEquals(1000, films.size());
    }

    @Test
    void find_all_actors_of_horror_films() {
        Result<Record2<String, String>> horrorFilms = dsl.select(ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
                .from(ACTOR)
                .join(FILM_ACTOR).on(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID.cast(Short.class)))
                .join(FILM).on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID.cast(Short.class)))
                .join(FILM_CATEGORY).on(FILM_CATEGORY.FILM_ID.eq(FILM.FILM_ID.cast(Short.class)))
                .join(CATEGORY).on(FILM_CATEGORY.CATEGORY_ID.eq(CATEGORY.CATEGORY_ID.cast(Short.class)))
                .where(CATEGORY.NAME.eq("Horror"))
                .groupBy(ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
                .orderBy(ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
                .fetch();

        Assertions.assertEquals(155, horrorFilms.size());
    }

    @Test
    void find_all_actors_of_horror_films_implicit_join() {
        Result<Record2<String, String>> horrorFilms = dsl.select(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .from(FILM_ACTOR)
                .join(FILM_CATEGORY).on(FILM_ACTOR.FILM_ID.eq(FILM_CATEGORY.FILM_ID))
                .where(FILM_CATEGORY.category().NAME.eq("Horror"))
                .groupBy(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .orderBy(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .fetch();

        Assertions.assertEquals(155, horrorFilms.size());
    }
}
