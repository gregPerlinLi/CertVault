package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import lombok.*;

import java.util.List;

/**
 * Login data transfer object
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code LoginDTO}
 * @date 2025/3/3 20:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class LoginResultDTO {

    /**
     * Username
     */
    private String username;

    /**
     * Display name
     */
    private String displayName;

    /**
     * roles
     */
    private Integer role;

    public LoginResultDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.role = user.getRole();
    }
}
