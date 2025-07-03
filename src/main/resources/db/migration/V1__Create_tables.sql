CREATE TABLE authors
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age  INT          NOT NULL
);

CREATE TABLE books
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    page_count INT          NOT NULL,
    author_id  INT REFERENCES authors (id)
);