CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) UNIQUE
    );

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE
    );

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR,
    category BIGINT REFERENCES categories(id),
    created_on TIMESTAMP,
    description VARCHAR,
    event_date TIMESTAMP,
    initiator BIGINT REFERENCES users(id),
    lat FLOAT,
    lon FLOAT,
    paid BOOL,
    participant_limit BIGINT,
    published_on TIMESTAMP,
    request_moderation BOOL,
    state VARCHAR(255),
    title VARCHAR(512) UNIQUE
    );

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP,
    event BIGINT REFERENCES events(id),
    requester BIGINT REFERENCES users(id),
    status VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOL,
    title VARCHAR(255) UNIQUE
    );

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT REFERENCES compilations(id),
    event_id BIGINT REFERENCES events(id),
    PRIMARY KEY (compilation_id, event_id)
    );

CREATE TABLE IF NOT EXISTS events_ratings (
    user_id BIGINT REFERENCES users(id),
    event_id BIGINT REFERENCES events(id),
    rating INTEGER NOT NULL,
    PRIMARY KEY (user_id, event_id)
    );