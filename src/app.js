const express = require('express');

const healthRoutes = require('./routes/healthRoutes');
const authRoutes = require('./routes/authRoutes');
const errorHandler = require('./middleware/errorMiddleware');
// const authenticateToken = require('./middleware/authMiddleware');
const gamesRoutes = require('./routes/gamesRoutes');

const app = express();

// Middlewares globales
app.use(express.json());

// Rutas
app.use('/health', healthRoutes);
app.use('/', authRoutes);
app.use('/games', gamesRoutes);

// Ruta no encontrada
app.use((req, res) => {
    res.status(404).json({
        message: 'Ruta no encontrada'
    });
});

// Manejo centralizado de errores
app.use(errorHandler);

module.exports = app;
