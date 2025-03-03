package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import lombok.*;

/**
 * Login data transfer object
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code LoginDTO}
 * @date 2025/3/3 22:39
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class LoginDTO {

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    public LoginDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

}
