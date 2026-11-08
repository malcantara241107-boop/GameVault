USE gamevault;

-- Usuarios
CREATE TABLE IF NOT EXISTS users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Videojuegos
CREATE TABLE IF NOT EXISTS games (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    developer VARCHAR(150) NOT NULL,
    genre VARCHAR(80) NOT NULL,
    release_year SMALLINT UNSIGNED NOT NULL,
    is_owned BOOLEAN NOT NULL DEFAULT FALSE,
    obtained_date DATE NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_date DATE NULL,
    rating DECIMAL(3,1) NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_games_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_game_rating
        CHECK (rating IS NULL OR rating BETWEEN 0 AND 10),

    CONSTRAINT chk_game_year
        CHECK (release_year BETWEEN 1950 AND 2100),

    CONSTRAINT chk_game_status
        CHECK (
            (is_owned = FALSE
                AND obtained_date IS NULL
                AND is_completed = FALSE
                AND completed_date IS NULL)
            OR
            (is_owned = TRUE
                AND obtained_date IS NOT NULL
                AND (
                    (is_completed = FALSE AND completed_date IS NULL)
                    OR
                    (is_completed = TRUE
                        AND completed_date IS NOT NULL
                        AND completed_date >= obtained_date)
                )
            )
        )
);

-- Plataformas de cada videojuego
CREATE TABLE IF NOT EXISTS game_platforms (
    game_id INT UNSIGNED NOT NULL,
    platform VARCHAR(80) NOT NULL,

    PRIMARY KEY (game_id, platform),

    CONSTRAINT fk_platforms_game
        FOREIGN KEY (game_id)
        REFERENCES games(id)
        ON DELETE CASCADE
);
