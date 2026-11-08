const pool = require('../config/database');

// Obtener todos los videojuegos de un usuario
const findAllByUser = async (userId) => {
    const [rows] = await pool.execute(
        `SELECT
            g.*,
            gp.platform
        FROM games g
        LEFT JOIN game_platforms gp
            ON g.id = gp.game_id
        WHERE g.user_id = ?
        ORDER BY g.id`,
        [userId]
    );

    return groupGames(rows);
};

// Obtener un videojuego específico de un usuario
const findByIdAndUser = async (gameId, userId) => {
    const [rows] = await pool.execute(
        `SELECT
            g.*,
            gp.platform
        FROM games g
        LEFT JOIN game_platforms gp
            ON g.id = gp.game_id
        WHERE g.id = ? AND g.user_id = ?`,
        [gameId, userId]
    );

    const games = groupGames(rows);

    return games[0] || null;
};

// Crear un videojuego y sus plataformas
const create = async (game, userId) => {
    const connection = await pool.getConnection();

    try {
        await connection.beginTransaction();

        // Insertar los datos generales del videojuego
        const [result] = await connection.execute(
            `INSERT INTO games (
                user_id,
                title,
                description,
                developer,
                genre,
                release_year,
                is_owned,
                obtained_date,
                is_completed,
                completed_date,
                rating
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            [
                userId,
                game.title,
                game.description,
                game.developer,
                game.genre,
                game.release_year,
                game.is_owned,
                game.obtained_date,
                game.is_completed,
                game.completed_date,
                game.rating
            ]
        );

        const gameId = result.insertId;

        // Insertar las plataformas asociadas
        for (const platform of game.platforms) {
            await connection.execute(
                `INSERT INTO game_platforms (
                    game_id,
                    platform
                ) VALUES (?, ?)`,
                [gameId, platform]
            );
        }

        await connection.commit();

        return await findByIdAndUser(gameId, userId);

    } catch (error) {
        await connection.rollback();
        throw error;

    } finally {
        connection.release();
    }
};

// Actualizar un videojuego y sus plataformas
const update = async (gameId, game, userId) => {
    const connection = await pool.getConnection();

    try {
        await connection.beginTransaction();

        // Comprobar que el videojuego pertenece al usuario
        const [existingGames] = await connection.execute(
            `SELECT id
             FROM games
             WHERE id = ? AND user_id = ?
             FOR UPDATE`,
            [gameId, userId]
        );

        if (existingGames.length === 0) {
            await connection.rollback();
            return null;
        }

        // Actualizar los datos principales
        await connection.execute(
            `UPDATE games SET
                title = ?,
                description = ?,
                developer = ?,
                genre = ?,
                release_year = ?,
                is_owned = ?,
                obtained_date = ?,
                is_completed = ?,
                completed_date = ?,
                rating = ?
            WHERE id = ? AND user_id = ?`,
            [
                game.title,
                game.description,
                game.developer,
                game.genre,
                game.release_year,
                game.is_owned,
                game.obtained_date,
                game.is_completed,
                game.completed_date,
                game.rating,
                gameId,
                userId
            ]
        );

        // Reemplazar las plataformas anteriores
        await connection.execute(
            'DELETE FROM game_platforms WHERE game_id = ?',
            [gameId]
        );

        for (const platform of game.platforms) {
            await connection.execute(
                `INSERT INTO game_platforms (game_id, platform)
                 VALUES (?, ?)`,
                [gameId, platform]
            );
        }

        await connection.commit();

        return await findByIdAndUser(gameId, userId);

    } catch (error) {
        await connection.rollback();
        throw error;

    } finally {
        connection.release();
    }
};

// Eliminar un videojuego
const remove = async (gameId, userId) => {
    const [result] = await pool.execute(
        `DELETE FROM games
         WHERE id = ? AND user_id = ?`,
        [gameId, userId]
    );

    return result.affectedRows > 0;
};

// Agrupar las plataformas pertenecientes a cada videojuego
const groupGames = (rows) => {
    const gamesMap = new Map();

    for (const row of rows) {
        if (!gamesMap.has(row.id)) {
            gamesMap.set(row.id, {
                id: row.id,
                user_id: row.user_id,
                title: row.title,
                description: row.description,
                developer: row.developer,
                genre: row.genre,
                release_year: row.release_year,
                is_owned: Boolean(row.is_owned),
                obtained_date: row.obtained_date,
                is_completed: Boolean(row.is_completed),
                completed_date: row.completed_date,
                rating: row.rating,
                created_at: row.created_at,
                updated_at: row.updated_at,
                platforms: []
            });
        }

        if (row.platform !== null) {
            gamesMap.get(row.id).platforms.push(row.platform);
        }
    }

    return Array.from(gamesMap.values());
};

module.exports = {
    findAllByUser,
    findByIdAndUser,
    create,
    update,
    remove
};
