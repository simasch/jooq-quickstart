# jOOQ Quickstart

This project introduces [jOOQ](https://jooq.org).

A PostgreSQL database with pre-installed Sakila schema and example data is used.

### Run the Database

It's recommend to use Docker to run the database:

    docker run -e POSTGRES_PASSWORD=sakila -p 5432:5432 -d simas/postgres-sakila 

Alternatively you can install PostgreSQL and then apply the two SQL script from https://github.com/simasch/docker-postgres-sakila

### Run the Build with Tests

The jOOQ Maven plugin generates the jOOQ classes from the database. 

    mvn test
