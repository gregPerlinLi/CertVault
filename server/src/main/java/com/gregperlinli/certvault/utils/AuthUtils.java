package com.gregperlinli.certvault.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

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


    public static String getGitHubUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                request,
                String.class
        );
        return response.getBody();
    }

}
