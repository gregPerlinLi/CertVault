package com.gregperlinli.certvault.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Authentication Utils
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code AuthUtils}
 * @date 2025/3/3 21:12
 */
public class AuthUtils {

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public static boolean matchesPassword(String rawPassword, String encryptedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    public static String roleIdToRoleName(Integer roleId) {
        return switch (roleId) {
            case 1 -> "USER";
            case 2 -> "ADMIN";
            case 3 -> "SUPERADMIN";
            default -> "GUEST";
        };
    }


}
