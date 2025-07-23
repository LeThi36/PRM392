package com.example.project2272.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    // Băm mật khẩu
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            return null;
        }
        // genSalt(12) tạo ra một muối (salt) và xác định độ khó (work factor) là 12.
        // Độ khó càng cao, thời gian băm càng lâu, chống brute-force tốt hơn.
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Xác minh mật khẩu
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
