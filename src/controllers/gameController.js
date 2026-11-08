const gameService = require('../services/gameService');

// Obtener todos los videojuegos del usuario autenticado
const getAllGames = async (req, res, next) => {
    try {
        const games = await gameService.getAllGames(req.user.id);

        res.status(200).json(games);
    } catch (error) {
        next(error);
    }
};

// Obtener un videojuego específico
const getGameById = async (req, res, next) => {
    try {
        const gameId = Number(req.params.id);

        if (!Number.isInteger(gameId) || gameId <= 0) {
            return res.status(400).json({
                message: 'El ID del videojuego no es válido'
            });
        }

        const game = await gameService.getGameById(
            gameId,
            req.user.id
        );

        res.status(200).json(game);
    } catch (error) {
        next(error);
    }
};

// Crear un videojuego
const createGame = async (req, res, next) => {
    try {
        const game = await gameService.createGame(
            req.body,
            req.user.id
        );

        res.status(201).json(game);

    } catch (error) {
        next(error);
    }
};

// Actualizar un videojuego
const updateGame = async (req, res, next) => {
    try {
        const gameId = Number(req.params.id);

        if (!Number.isInteger(gameId) || gameId <= 0) {
            return res.status(400).json({
                message: 'El ID del videojuego no es válido'
            });
        }

        const game = await gameService.updateGame(
            gameId,
            req.body,
            req.user.id
        );

        res.status(200).json(game);

    } catch (error) {
        next(error);
    }
};

// Eliminar un videojuego
const deleteGame = async (req, res, next) => {
    try {
        const gameId = Number(req.params.id);

        if (!Number.isInteger(gameId) || gameId <= 0) {
            return res.status(400).json({
                message: 'El ID del videojuego no es válido'
            });
        }

        await gameService.deleteGame(
            gameId,
            req.user.id
        );

        res.status(204).send();

    } catch (error) {
        next(error);
    }
};

module.exports = {
    getAllGames,
    getGameById,
    createGame,
    updateGame,
    deleteGame
};
