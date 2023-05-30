create table languages
(
    language_id serial
        primary key,
    code        varchar(3) not null,
    name        varchar(50),
    flag_url    varchar(255)
);

alter table languages
    owner to postgres;

create table words
(
    word_id     bigserial
        primary key,
    language_id integer      not null
        constraint words_language_fk
            references languages,
    word_length integer      not null,
    word_number integer      not null,
    word        varchar(255) not null
);

alter table words
    owner to postgres;

create table users
(
    user_id serial not null
        primary key,
    login varchar(50) not null,
    password_hash varchar(64) not null
);

-- Funky Wunky way to start the sequence from 2
-- Create a new temporary sequence starting from 2
CREATE SEQUENCE temp_user_id_seq START 2;

-- Update user_id column with values from the temporary sequence
UPDATE users
SET user_id = nextval('temp_user_id_seq');

-- Alter user_id column to use the temporary sequence as its default value
ALTER TABLE users
ALTER COLUMN user_id SET DEFAULT nextval('temp_user_id_seq');

--  Drop the existing sequence and rename the temporary sequence
DROP SEQUENCE users_user_id_seq;
ALTER SEQUENCE temp_user_id_seq RENAME TO users_user_id_seq;

alter table users
    owner to postgres;


create table sessions
(
    session_id   bigserial
        primary key,
    user_id      integer   not null
        constraint sessions_user_fk
            references users,
    language_id  integer   not null
        constraint sessions_language_fk
            references languages,
    word_length  integer   not null,
    word_number  integer   not null,
    created_date timestamp not null
);

alter table sessions
    owner to postgres;

create table guesses
(
    guess_id     serial
        primary key,
    session_id   bigint    not null
        constraint guesses_session_fk
            references sessions,
    guess_number integer   not null,
    guess        text      not null,
    created_date timestamp not null
);

alter table guesses
    owner to postgres;

-- The result of a single game
    create table results
(
    result_id     serial
        primary key,
    user_id   integer      not null
        constraint results_user_fk
            references sessions,
    result      boolean    not null,
    created_date timestamp not null,
    guess_number integer

);

alter table results
    owner to postgres;


