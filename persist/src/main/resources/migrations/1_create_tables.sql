-- Here is our first SQL script. In this one, we'll be creating the database and two tables with
-- a simple setup. There are a couple of ways to run this.
-- 1. You can set up the SQL driver in IntelliJ, right click this file, and say run. Then you'll
--      have to make sure you select the ALREADY CREATED database (see below). In the IntelliJ
--      database tool, you may need to right click on the top-level connection, go to database tools,
--      and say manage shown schemas for the new database to pop up.
-- 2. You can open up this file in PGAdmin and run it there.
-- 3. You can start Postgres up in your terminal by running psql -U <username> and look at the
--      commands to run it there.

-- You'll also want to create the database on your own by running
-- CREATE DATABASE jumpstart;

-- Create our posts table.
CREATE TABLE IF NOT EXISTS posts (
    -- In other databases, SERIAL is usually an INTEGER with an AUTO INCREMENT modifier.
    -- The PRIMARY KEY tells the database that that is the ID for this table. It can also be set in
    -- a CONSTRAINT, which we'll show later on.
    -- The spacing we're using here doesn't have any purpose other than to make
    -- this look more readable.
    id              SERIAL          PRIMARY KEY,
    -- A VARCHAR is like a string. The 255 is its max length. CHAR(5) and VARCHAR(5)
    -- are similar in that they both hold text of length 5, but the difference is that
    -- CHAR will pad the remaining space with spaces if all 5 characters aren't used.
    author          VARCHAR(255)    NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    -- TEXT is like CHAR and VARCHAR except it doesn't perform length checking.
    body            TEXT            NOT NULL,
    -- Our DATETIME type. Also, instead of camelCasing, we use snake_case in SQL scripts.
    -- We also set the default value to be the CURRENT_TIMESTAMP.
    date_created    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    clap_count      INTEGER         DEFAULT 0
-- Don't forget to include the semicolon at the end of this.
);

-- Create our comments table.
CREATE TABLE IF NOT EXISTS comments (
    id              SERIAL          PRIMARY KEY,
    -- Since this is our primary key of the posts table, this will end up being a foreign key. We'll
    -- talk about that later on.
    post_id         INTEGER         NOT NULL,
    author          VARCHAR(255)    NOT NULL,
    -- Since this is a comment, let's limit this one.
    body            VARCHAR(1000)   NOT NULL,
    date_created    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    clap_count      INTEGER         DEFAULT 0
);

