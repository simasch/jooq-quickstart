package ch.martinelli.jooq.quickstart;

import ch.martinelli.jooq.quickstart.database.tables.Actor;
import ch.martinelli.jooq.quickstart.database.tables.Category;
import ch.martinelli.jooq.quickstart.database.tables.Film;
import ch.martinelli.jooq.quickstart.database.tables.FilmActor;
import ch.martinelli.jooq.quickstart.database.tables.FilmCategory;
import ch.martinelli.jooq.quickstart.database.tables.records.FilmRecord;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.martinelli.jooq.quickstart.database.tables.Actor.ACTOR;
import static ch.martinelli.jooq.quickstart.database.tables.Category.CATEGORY;
import static ch.martinelli.jooq.quickstart.database.tables.Film.FILM;
import static ch.martinelli.jooq.quickstart.database.tables.FilmActor.FILM_ACTOR;
import static ch.martinelli.jooq.quickstart.database.tables.FilmCategory.FILM_CATEGORY;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
class QueryTest {

    @Autowired
    private DSLContext dsl;

    @Test
    void find_all_films() {
        Result<FilmRecord> films = dsl.selectFrom(FILM).fetch();

        assertEquals(1000, films.size());
    }

    @Test
    void find_all_actors_of_horror_films() {
        Result<Record2<String, String>> actorsOfHorrorFilms = dsl
                .select(ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
                .from(ACTOR)
                .join(FILM_ACTOR).on(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID.cast(Short.class)))
                .join(FILM).on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID.cast(Short.class)))
                .join(FILM_CATEGORY).on(FILM_CATEGORY.FILM_ID.eq(FILM.FILM_ID.cast(Short.class)))
                .join(CATEGORY).on(FILM_CATEGORY.CATEGORY_ID.eq(CATEGORY.CATEGORY_ID.cast(Short.class)))
                .where(CATEGORY.NAME.eq("Horror"))
                .groupBy(ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
                .orderBy(ACTOR.FIRST_NAME, ACTOR.LAST_NAME)
                .fetch();

        assertEquals(155, actorsOfHorrorFilms.size());
    }

    @Test
    void find_all_actors_of_horror_films_implicit_join() {
        Result<Record2<String, String>> actorsOfHorrorFilms = dsl
                .select(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .from(FILM_ACTOR)
                .join(FILM_CATEGORY).on(FILM_ACTOR.FILM_ID.eq(FILM_CATEGORY.FILM_ID))
                .where(FILM_CATEGORY.category().NAME.eq("Horror"))
                .groupBy(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .orderBy(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .fetch();

        assertEquals(155, actorsOfHorrorFilms.size());
    }

    @Test
    void find_all_actors_of_horror_films_implicit_join_into_record() {
        List<ActorWithFirstAndLastName> actorsOfHorrorFilms = dsl
                .select(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .from(FILM_ACTOR)
                .join(FILM_CATEGORY).on(FILM_ACTOR.FILM_ID.eq(FILM_CATEGORY.FILM_ID))
                .where(FILM_CATEGORY.category().NAME.eq("Horror"))
                .groupBy(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .orderBy(FILM_ACTOR.actor().FIRST_NAME, FILM_ACTOR.actor().LAST_NAME)
                .fetchInto(ActorWithFirstAndLastName.class);

        assertEquals(155, actorsOfHorrorFilms.size());
    }

    @Test
    void insert_film() {
        int insertedRows = dsl.
                insertInto(FILM)
                .columns(FILM.TITLE, FILM.LANGUAGE_ID)
                .values("Test", (short) 1)
                .execute();

        assertEquals(1, insertedRows);
    }

    @Test
    void insert_film_using_record() {
        FilmRecord filmRecord = dsl.newRecord(FILM);
        filmRecord.setTitle("Test");
        filmRecord.setLanguageId((short) 1);
        int insertedRows = filmRecord.store();

        assertEquals(1, insertedRows);
    }

    @Test
    void find_film() {
        FilmRecord filmRecord = dsl
                .selectFrom(FILM)
                .where(FILM.FILM_ID.eq(1))
                .fetchOne();

        assertNotNull(filmRecord);
        assertEquals("ACADEMY DINOSAUR", filmRecord.getTitle());
    }

    @Test
    void find_all_actors_with_films() {
        List<ActorWithFilms> actorWithFilms = dsl
                .select(
                        ACTOR.FIRST_NAME,
                        ACTOR.LAST_NAME,
                        multiset(dsl
                                .select(FILM_ACTOR.film().TITLE)
                                .from(FILM_ACTOR)
                                .where(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID.cast(Short.class))))
                                .convertFrom(r -> r.map(mapping(ActorWithFilms.FilmWithName::new)))
                )
                .from(ACTOR)
                .fetch(mapping(ActorWithFilms::new));

        assertEquals(200, actorWithFilms.size());
    }
}
