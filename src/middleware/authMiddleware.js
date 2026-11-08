const jwt = require('jsonwebtoken');
const userRepository = require('../repositories/userRepository');

const authenticateToken = async (req, res, next) => {

    // Obtener el token de la cabecera Authorization
    const authHeader = req.headers.authorization;

    if (!authHeader) {
        return res.status(401).json({
            message: 'Token no proporcionado'
        });
    }

    const [scheme, token] = authHeader.split(' ');

    if (scheme !== 'Bearer' || !token) {
        return res.status(401).json({
            message: 'Formato de autorización inválido'
        });
    }

    try {
        // Verificar el JWT
        const decoded = jwt.verify(
            token,
            process.env.JWT_SECRET
        );

        // Buscar al usuario asociado al token
        const user = await userRepository.findById(
            Number(decoded.sub)
        );

        if (!user) {
            return res.status(401).json({
                message: 'Usuario no válido'
            });
        }

        // Guardar el usuario autenticado
        req.user = user;

        // Continuar con el siguiente middleware o controlador
        next();

    } catch (error) {

        if (
            error.name === 'JsonWebTokenError' ||
            error.name === 'TokenExpiredError'
        ) {
            return res.status(401).json({
                message: 'Token inválido o expirado'
            });
        }

        next(error);
    }
};

module.exports = authenticateToken;
