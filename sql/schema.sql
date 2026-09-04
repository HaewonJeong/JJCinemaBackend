--26.08.23 업데이트
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE users (
                       user_id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_by VARCHAR(50)
);
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE genres (
                        genre_id BIGSERIAL PRIMARY KEY,
                        genre_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE ratings (
                         rating_id BIGINT PRIMARY KEY,
                         name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE movies (
                        movie_id BIGSERIAL PRIMARY KEY,
                        title VARCHAR(200) NOT NULL,
                        genre_id BIGINT NOT NULL REFERENCES genres(genre_id),
                        runtime INT NOT NULL,
                        rating_id BIGINT NOT NULL REFERENCES ratings(rating_id),
                        director VARCHAR(100),
                        release_date DATE NOT NULL,
                        poster_base64 TEXT,
                        synopsis TEXT,
                        status VARCHAR(20) NOT NULL,
                        created_by VARCHAR(50),
                        updated_by VARCHAR(50),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER trg_movies_updated_at
    BEFORE UPDATE ON movies
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE INDEX idx_movies_status ON movies(status);
CREATE INDEX idx_movies_genre ON movies(genre_id);
CREATE INDEX idx_movies_rating ON movies(rating_id);

CREATE TABLE showtimes (
                           showtime_id BIGSERIAL PRIMARY KEY,
                           movie_id BIGINT NOT NULL REFERENCES movies(movie_id),
                           date DATE NOT NULL,
                           time TIME NOT NULL,
                           theater VARCHAR(20) NOT NULL,
                           price INT NOT NULL,
                           created_by VARCHAR(50),
                           updated_by VARCHAR(50),
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
                          booking_id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL REFERENCES users(user_id),
                          showtime_id BIGINT NOT NULL REFERENCES showtimes(showtime_id),
                          status VARCHAR(20) NOT NULL,
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
                               booking_seat_id BIGSERIAL PRIMARY KEY,
                               booking_id BIGINT NOT NULL REFERENCES bookings(booking_id),
                               showtime_id BIGINT NOT NULL REFERENCES showtimes(showtime_id),
                               seat_code VARCHAR(5) NOT NULL,
                               UNIQUE (showtime_id, seat_code)
);
CREATE INDEX idx_booking_seats_booking ON booking_seats(booking_id);

CREATE TABLE payments (
                          payment_id BIGSERIAL PRIMARY KEY,
                          booking_id BIGINT NOT NULL UNIQUE REFERENCES bookings(booking_id),
                          amount INT NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          method VARCHAR(20),
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER trg_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE seats (
                       seat_id BIGSERIAL PRIMARY KEY,
                       showtime_id BIGINT NOT NULL REFERENCES showtimes(showtime_id),
                       seat_code VARCHAR(5) NOT NULL,
                       occupied BOOLEAN NOT NULL DEFAULT FALSE,
                       held_at TIMESTAMP,
                       UNIQUE (showtime_id, seat_code)
);
CREATE INDEX idx_seats_showtime ON seats(showtime_id);

INSERT INTO seats (showtime_id, seat_code, occupied)
SELECT s.showtime_id, codes.code, FALSE
FROM showtimes s
         CROSS JOIN (
    VALUES ('A1'),('A2'),('A3'),('A4'),('A5'),('A6'),('A7'),('A8'),
           ('B1'),('B2'),('B3'),('B4'),('B5'),('B6'),('B7'),('B8'),
           ('C1'),('C2'),('C3'),('C4'),('C5'),('C6'),('C7'),('C8'),
           ('D1'),('D2'),('D3'),('D4'),('D5'),('D6'),('D7'),('D8'),
           ('E1'),('E2'),('E3'),('E4'),('E5'),('E6'),('E7'),('E8')
) AS codes(code)
ON CONFLICT (showtime_id, seat_code) DO NOTHING;

SELECT setval('genres_genre_id_seq', (SELECT MAX(genre_id) FROM genres));
SELECT setval('ratings_rating_id_seq', (SELECT MAX(rating_id) FROM ratings));
SELECT setval(pg_get_serial_sequence('genres', 'genre_id'),
              COALESCE((SELECT MAX(genre_id) FROM genres), 1));

SELECT setval(pg_get_serial_sequence('ratings', 'rating_id'),
              COALESCE((SELECT MAX(rating_id) FROM ratings), 1));