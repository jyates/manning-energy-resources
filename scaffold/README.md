# User scaffolding and tools

## Getting started

This template should help you get started quickly with this Manning LiveProject. It is designed
to support Java 8 or Scala 12. It builds code using [Maven 3.x](http://maven.apache.org). 

Your application code can be written under `src/main/[java|scala]` (depending on the language you
 are choosing). Similarly test code lives under `src/test/[java|scala]` 
 
Some quick maven commands:

 * print maven version `mvn -version`
 * compile: `mvn compile`
 * cleaning up: `mvn clean` 
 * building the final jar: `mvn jar`
   ** Creates a ar file in the `target/` directory, which can then be run.
   ** Ex: `java -cp target/myproject-1.0-SNAPSHOT.jar com.manning.MyApplication`
 * run tests: `mvn test` 

 
You will be asked to create [Avro](http://avro.apache.org) schemas, those can be added under `src
/main/resources/avro` and will automatically generate the Java implementations of the Avro
 schemas under `src/main/generated` on calls to compile. To just generate the Java
 implementations, you can use the maven command:

```bash
$ mvn clean generate-sources
``` 

### A note on tests

All test classes must end in `Test` for both Scala and Java. For example, `ExampleJavaTest` will
 run, but `ExampleTestJava` will not be found by maven as a test class to execute. 

## Local Kafka & Confluent Schema Registry

```bash
$ docker-compose -f docker-compose-kafka.yml up
```

Kafka will then be running in the docker container, and the bootstrap.server property `localhost:29092`.
The schema registry is available at `localhost:8090`.

When the producer attempts to send data to the broker, it will be automatically created.


## Databases

### Local PostrgeSQL Database

Based on the image at https://hub.docker.com/_/postgres

```bash
$ docker run --name manning-postgres -e POSTGRES_PASSWORD=secret -p 5432:5432 -d postgres:12.2
```

Run the latest (as of writing) available Postgres container, named `manning-postgres`, where the
`postgres` user's password is `secret`.

The container is now running and accessible on port `5432`. 

You can connect to the command-line in the container (to add/modify databases and tables) with
```bash
$  docker exec -it manning-postgres psql -U postgres 
```

Listing schemas ([schema vs database](https://www.postgresql.org/docs/8.1/ddl-schemas.html)):
```bash
$ docker exec -it postgres psql -U postgres -c "SELECT schema_name FROM information_schema.schemata"
    schema_name     
--------------------
 pg_toast
 pg_temp_1
 pg_toast_temp_1
 pg_catalog
 public
 information_schema
(7 rows)
```

Or create a new table ([create table docs](https://www.postgresql.org/docs/9.1/sql-createtable.html))
, in the public database:
```bash
$  docker exec -it manning-postgres psql -U postgres \
  -c "CREATE TABLE devices (uuid varchar, state boolean)"
```

And list tables
```bash
$  docker exec -it manning-postgres psql -U postgres -c '\dt' 
          List of relations
 Schema |  Name   | Type  |  Owner   
--------+---------+-------+----------
 public | devices | table | postgres
(1 row)
```

You can stop and remove the container with
 
```bash
$ docker stop manning-postgres
$ docker rm manning-postgres
```

Finally, enable the `postgres` dependency in the pom, adding the client libraries to the project
, allowing you to actually connect to the database

#### Dropwizard connection properties

The properties you need to add to your dropwizard configuration are

```yaml
database:
  driverClass: org.postgresql.Driver
  user: postgres
  password: secret
  url: "jdbc:postgresql://0.0.0.0:5432/postgres"
```
### Local MySQL Database

Based on the image at https://hub.docker.com/_/mysql

Run the latest (as of writing) available MySQL container, named `manning-mysql`, where the
`root` user's password is `secret-pw`.

```bash
$ docker run --name manning-mysql -e MYSQL_ROOT_PASSWORD=secret -p 3306:3306 -d mysql:8.0.19
```

The container is now running and accessible on port `3306`. 

You can access the MySQL command line (to do things like creating a database or a table) with

```bash
$ docker exec -it manning-mysql mysql -P 3306 -u root --password=secret
```

You can stop and remove the container with
 
```bash
$ docker stop manning-mysql
$ docker rm manning-mysql
```


Finally, enable the `mysql` dependency in the pom, adding the client libraries to the project
, allowing you to actually connect to the database

#### Dropwizard connection properties

```yaml
database:
  driverClass: com.mysql.cj.jdbc.Driver
  user: root
  password: secret
  url: "jdbc:mysql://0.0.0.0:3306/information_schema"
```
