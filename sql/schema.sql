-- PostgreSQL 버전 스키마 (MySQL 원본을 변환)
-- updated_at 자동 갱신은 MySQL의 "ON UPDATE CURRENT_TIMESTAMP"가 PostgreSQL엔 없어서
-- 트리거 함수(set_updated_at)로 동일하게 동작하도록 구현함

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- CUSTOMER / ADMIN
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE genres (
    genres_id BIGSERIAL PRIMARY KEY,
    genres_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    genre_id BIGINT NOT NULL,
    runtime INT NOT NULL,
    rating_id BIGINT NOT NULL,
    director VARCHAR(100),
    release_date DATE NOT NULL,
    poster_base64 TEXT,
    synopsis TEXT,
    status VARCHAR(20) NOT NULL, -- SHOWING / UPCOMING
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_movies_updated_at
    BEFORE UPDATE ON movies
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_movies_genre ON movies(genre_id);

CREATE TABLE showtimes (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    theater VARCHAR(20) NOT NULL,
    price INT NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (theater, date, time)
);

CREATE TRIGGER trg_showtimes_updated_at
    BEFORE UPDATE ON showtimes
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_showtimes_movie ON showtimes(movie_id);
CREATE INDEX idx_showtimes_date ON showtimes(date);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL, -- HOLD / PAID / CANCELLED
    held_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    booked_at TIMESTAMP,
    total_price INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_showtime ON bookings(showtime_id);
CREATE INDEX idx_bookings_status ON bookings(status);

CREATE TABLE booking_seats (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    seat_code VARCHAR(5) NOT NULL,
    UNIQUE (showtime_id, seat_code)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL, -- SUCCESS / FAILED / REFUNDED
    method VARCHAR(20),
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
