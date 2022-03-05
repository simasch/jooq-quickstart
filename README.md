# jOOQ Quickstart

## PostgreSQL

The example uses PostgreSQL database. It's run in a docker container with preintalled Sakila schema and example data.
https://github.com/fspacek/docker-postgres-sakila

### Run the Database

    docker run -e POSTGRES_PASSWORD=sakila -p 5432:5432 -d simas/postgres-sakila 
