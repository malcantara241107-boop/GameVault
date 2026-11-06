const express = require('express');

const app = express();

// Middlewares
app.use(express.json());

// Ruta de prueba
app.get('/health', (req, res) => {
    res.status(200).json({
        status: 'ok',
        message: 'GameVault API funcionando correctamente'
    });
});

// Ruta no encontrada
app.use((req, res) => {
    res.status(404).json({
        message: 'Ruta no encontrada'
    });
});

module.exports = app;
