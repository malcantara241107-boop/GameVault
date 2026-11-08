const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const userRepository = require('../repositories/userRepository');

const register = async (name, email, password) => {
    const normalizedEmail = email.trim().toLowerCase();

    // Comprobar si el correo ya está registrado
    const existingUser = await userRepository.findByEmail(normalizedEmail);

    if (existingUser) {
        const error = new Error('El correo ya está registrado');
        error.statusCode = 409;
        throw error;
    }

    // Generar hash de la contraseña
    const passwordHash = await bcrypt.hash(password, 12);

    // Crear el usuario
    const user = await userRepository.create(
        name.trim(),
        normalizedEmail,
        passwordHash
    );

    // Generar JWT
    const token = jwt.sign(
        { sub: String(user.id) },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN || '1h' }
    );

    return {
    token,
    user: {
        id: user.id,
        name: user.name,
        email: user.email
    }
};
};

const login = async (email, password) => {
    const normalizedEmail = email.trim().toLowerCase();

    // Buscar al usuario
    const user = await userRepository.findByEmail(normalizedEmail);

    if (!user) {
        const error = new Error('Credenciales incorrectas');
        error.statusCode = 401;
        throw error;
    }

    // Comparar contraseña con el hash almacenado
    const passwordMatches = await bcrypt.compare(
        password,
        user.password_hash
    );

    if (!passwordMatches) {
        const error = new Error('Credenciales incorrectas');
        error.statusCode = 401;
        throw error;
    }

    // Generar JWT
    const token = jwt.sign(
        { sub: String(user.id) },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN || '1h' }
    );

    return {
        token,
        user: {
            id: user.id,
            name: user.name,
            email: user.email
        }
    };
};

module.exports = {
    register,
    login
};
