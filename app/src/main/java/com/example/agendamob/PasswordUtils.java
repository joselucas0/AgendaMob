package com.example.agendamob;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Gera um hash da senha
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Verifica a senha com o hash armazenado
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
