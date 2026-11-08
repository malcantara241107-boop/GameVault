const authService = require('../services/authService');

const register = async (req, res, next) => {
    try {
        const { name, email, password } = req.body;

        // Validar campos obligatorios
        if (!name || !email || !password) {
            return res.status(400).json({
                message: 'Todos los campos son obligatorios'
            });
        }

        // Validar formato básico del correo
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(email)) {
            return res.status(400).json({
                message: 'El correo electrónico no es válido'
            });
        }

        // Validar longitud de contraseña
        if (password.length < 8) {
            return res.status(400).json({
                message: 'La contraseña debe tener al menos 8 caracteres'
            });
        }

        const result = await authService.register(
            name,
            email,
            password
        );

        res.status(201).json(result);

    } catch (error) {
        next(error);
    }
};

const login = async (req, res, next) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({
                message: 'Correo y contraseña son obligatorios'
            });
        }

        const result = await authService.login(email, password);

        res.status(200).json(result);

    } catch (error) {
        next(error);
    }
};

module.exports = {
    register,
    login
};
