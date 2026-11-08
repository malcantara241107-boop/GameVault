const gameRepository = require('../repositories/gameRepository');
const {
    PLATFORMS,
    GENRES
} = require('../constants/gameOptions');

// Generar un error de validación
const validationError = (message) => {
    const error = new Error(message);
    error.statusCode = 400;
    return error;
};

// Validar formato de fecha YYYY-MM-DD
const isValidDate = (value) => {
    if (typeof value !== 'string') return false;

    const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);

    if (!match) return false;

    const [, year, month, day] = match;
    const date = new Date(Date.UTC(
        Number(year),
        Number(month) - 1,
        Number(day)
    ));

    return (
        date.getUTCFullYear() === Number(year) &&
        date.getUTCMonth() === Number(month) - 1 &&
        date.getUTCDate() === Number(day)
    );
};

// Obtener la fecha actual en formato YYYY-MM-DD
const getToday = () => {
    const date = new Date();

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
};

// Obtener todos los videojuegos de un usuario
const getAllGames = async (userId) => {
    return await gameRepository.findAllByUser(userId);
};

// Obtener un videojuego específico de un usuario
const getGameById = async (gameId, userId) => {
    const game = await gameRepository.findByIdAndUser(
        gameId,
        userId
    );

    if (!game) {
        const error = new Error('Videojuego no encontrado');
        error.statusCode = 404;
        throw error;
    }

    return game;
};

// Validar y normalizar los datos de un videojuego
const validateGame = (game) => {

    // Validar título
    if (
        typeof game.title !== 'string' ||
        !game.title.trim()
    ) {
        throw validationError('El título es obligatorio');
    }

    // Validar descripción
    if (
        game.description !== undefined &&
        game.description !== null &&
        typeof game.description !== 'string'
    ) {
        throw validationError('La descripción no es válida');
    }

    // Validar desarrollador
    if (
        typeof game.developer !== 'string' ||
        !game.developer.trim()
    ) {
        throw validationError('El desarrollador es obligatorio');
    }

    // Validar género
    if (!GENRES.includes(game.genre)) {
        throw validationError('El género seleccionado no es válido');
    }

    // Validar plataformas
    if (
        !Array.isArray(game.platforms) ||
        game.platforms.length === 0 ||
        !game.platforms.every(platform => PLATFORMS.includes(platform)) ||
        new Set(game.platforms).size !== game.platforms.length
    ) {
        throw validationError('Las plataformas no son válidas');
    }

    // Validar año de lanzamiento
    if (
        !Number.isInteger(game.release_year) ||
        game.release_year < 1950 ||
        game.release_year > 2100
    ) {
        throw validationError('El año de lanzamiento no es válido');
    }

    // Validar estados booleanos
    if (
        typeof game.is_owned !== 'boolean' ||
        typeof game.is_completed !== 'boolean'
    ) {
        throw validationError('Los estados del videojuego no son válidos');
    }

    // Validar propiedad y fecha de obtención
    if (!game.is_owned) {
        if (
            game.obtained_date != null ||
            game.is_completed ||
            game.completed_date != null
        ) {
            throw validationError(
                'Un videojuego no adquirido no puede estar completado ni tener fechas de obtención'
            );
        }
    } else {
        if (
            !isValidDate(game.obtained_date) ||
            game.obtained_date > getToday()
        ) {
            throw validationError('La fecha de obtención no es válida');
        }
    }

    // Validar fecha de finalización
    if (game.is_completed) {
        if (
            !game.is_owned ||
            !isValidDate(game.completed_date) ||
            game.completed_date > getToday() ||
            game.completed_date < game.obtained_date
        ) {
            throw validationError(
                'La fecha de finalización no es válida'
            );
        }
    } else if (game.completed_date != null) {
        throw validationError(
            'Un videojuego no completado no puede tener fecha de finalización'
        );
    }

    // Validar calificación
    if (
        game.rating !== undefined &&
        game.rating !== null &&
        (
            typeof game.rating !== 'number' ||
            !Number.isFinite(game.rating) ||
            game.rating < 0 ||
            game.rating > 10
        )
    ) {
        throw validationError('La calificación debe estar entre 0 y 10');
    }

    // Preparar datos normalizados
    return {
        ...game,
        title: game.title.trim(),
        description: game.description?.trim() || null,
        developer: game.developer.trim(),
        platforms: game.platforms,
        obtained_date: game.obtained_date ?? null,
        completed_date: game.completed_date ?? null,
        rating: game.rating ?? null
    };
};

// Crear un videojuego
const createGame = async (game, userId) => {
    const normalizedGame = validateGame(game);

    return await gameRepository.create(normalizedGame, userId);
};

// Actualizar un videojuego
const updateGame = async (gameId, game, userId) => {

    // Validar y normalizar los datos
    const normalizedGame = validateGame(game);

    // Comprobar que el videojuego existe y pertenece al usuario
    const existingGame = await gameRepository.findByIdAndUser(
        gameId,
        userId
    );

    if (!existingGame) {
        const error = new Error('Videojuego no encontrado');
        error.statusCode = 404;
        throw error;
    }

    // Actualizar el videojuego
    return await gameRepository.update(
        gameId,
        normalizedGame,
        userId
    );
};

// Eliminar un videojuego
const deleteGame = async (gameId, userId) => {

    const deleted = await gameRepository.remove(
        gameId,
        userId
    );

    if (!deleted) {
        const error = new Error('Videojuego no encontrado');
        error.statusCode = 404;
        throw error;
    }

    return true;
};

module.exports = {
    getAllGames,
    getGameById,
    createGame,
    updateGame,
    deleteGame
};
