const express = require('express');
const authenticateToken = require('../middleware/authMiddleware');

const {
    getAllGames,
    getGameById,
    createGame,
    updateGame,
    deleteGame
} = require('../controllers/gameController');

const router = express.Router();

// Proteger todas las rutas de videojuegos
router.use(authenticateToken);

// Crear un videojuego
router.post('/', createGame);

// Actualizar un videojuego
router.put('/:id', updateGame);

// Eliminar un videojuego
router.delete('/:id', deleteGame);

// Obtener todos los videojuegos del usuario
router.get('/', getAllGames);

// Obtener un videojuego específico
router.get('/:id', getGameById);

module.exports = router;
