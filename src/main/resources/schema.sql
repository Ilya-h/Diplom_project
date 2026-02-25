-- Удаление таблиц (в обратном порядке создания из-за внешних ключей)
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS cinema_halls;

-- Создание таблицы cinema_halls
CREATE TABLE IF NOT EXISTS cinema_halls (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    total_seats INT NOT NULL,
    capacity INT,
    hall_type VARCHAR(255),
    rows_count INT,
    seats_per_row INT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы movies
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    duration_minutes INT,
    genre VARCHAR(255),
    director VARCHAR(255),
    actors VARCHAR(255),
    age_rating VARCHAR(255),
    poster_url VARCHAR(255),
    release_date DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы sessions
CREATE TABLE IF NOT EXISTS sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    start_time DATETIME(6) NOT NULL,
    end_time DATETIME(6) NOT NULL,
    price DOUBLE NOT NULL,
    format VARCHAR(255),
    hall_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (hall_id) REFERENCES cinema_halls(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы seats
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT NOT NULL AUTO_INCREMENT,
    hall_id BIGINT NOT NULL,
    row_num INT NOT NULL,
    seat_num INT NOT NULL,
    seat_type VARCHAR(255) NOT NULL,
    is_available BIT(1) NOT NULL,
    session_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (hall_id) REFERENCES cinema_halls(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы payments
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DOUBLE NOT NULL,
    payment_date DATETIME(6) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    transaction_id VARCHAR(255),
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    price DOUBLE NOT NULL,
    purchase_date DATETIME(6) NOT NULL,
    status VARCHAR(255) NOT NULL,
    ticket_number VARCHAR(255) NOT NULL,
    payment_id BIGINT,
    seat_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE SET NULL,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_ticket_number (ticket_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Создание таблицы reviews
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comment VARCHAR(255),
    rating INT NOT NULL,
    review_date DATETIME(6) NOT NULL,
    movie_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
