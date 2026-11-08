const pool = require('../config/database');

const getHealth = (req, res) => {
    res.status(200).json({
        status: 'ok',
        message: 'GameVault API funcionando correctamente'
    });
};

const getDatabaseHealth = async (req, res) => {
    try {
        const [rows] = await pool.query(`
            SELECT TABLE_NAME
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
        `);

        const tables = rows.map(row => row.TABLE_NAME);

        const requiredTables = [
            'users',
            'games',
            'game_platforms'
        ];

        const missingTables = requiredTables.filter(
            table => !tables.includes(table)
        );

        if (missingTables.length > 0) {
            return res.status(503).json({
                status: 'error',
                message: 'Faltan tablas en la base de datos',
                missingTables
            });
        }

        res.status(200).json({
            status: 'ok',
            database: 'connected',
            tables
        });

    } catch (error) {
        console.error('Error al consultar MySQL:', error.message);

        res.status(503).json({
            status: 'error',
            message: 'No se pudo conectar con la base de datos'
        });
    }
};

module.exports = {
    getHealth,
    getDatabaseHealth
};
