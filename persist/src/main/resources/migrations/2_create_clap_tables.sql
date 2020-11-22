-- This is our second migration! The reason we're writing it in a separate SQL script is so that you
-- can any scripts not previously run without having to drop and recreate all of the tables in your
-- database. This is important because it lets you preserve the data you've already collected
-- (otherwise, any data you previously had would have been deleted).

-- So, if you already ran your "1_create_tables.sql" script a few days ago, you can run this one and
-- have it build off of the database structure you already had.

CREATE TABLE IF NOT EXISTS post_claps (
    id              SERIAL      PRIMARY KEY,
    post_id         INT         NOT NULL,
    date_created    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

-- This creates a constraint so that every post_id column entry must have a valid entry in the
-- posts table with the same id. This also prevents an entry in the posts table from being deleted
-- if dependent foreign key entries exist in another table (unless
-- "ON [UPDATE/DELETE] [NO ACTION/CASCADE/SET NULL/SET DEFAULT]" is specified).

-- Create a constraint with post_claps_post_id_fk as the name (fk stands for foreign key).
    CONSTRAINT post_claps_post_id_fk
        -- The constraint type is a foreign key, and it's on the post_id column.
        FOREIGN KEY (post_id)
        -- The column references the id column in the posts table.
        REFERENCES posts (id)
        -- If an entry in the posts table is deleted, then all entries with the same post id in this
        -- table will be deleted too.
        ON DELETE CASCADE
);

-- Do the same for comments.
CREATE TABLE IF NOT EXISTS comment_claps (
    id              SERIAL      PRIMARY KEY,
    post_id         INT         NOT NULL,
    comment_id      INT         NOT NULL,
    date_created    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT comment_claps_post_id_fk
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE,

    CONSTRAINT comment_claps_comment_id_fk
        FOREIGN KEY (comment_id)
        REFERENCES comments (id)
        ON DELETE CASCADE
);

-- Now that we have a table for holding clap information, we want to get rid of the column
-- (since this can do more, like hold information on when claps were created) on the original
-- tables.

-- Alter the posts table (we can also change column data types, names, constraints, and other
-- things).
ALTER TABLE posts
    -- Get rid of the clap_count column.
    DROP COLUMN clap_count;

-- Do the same for comments.
ALTER TABLE comments
    DROP COLUMN clap_count;
