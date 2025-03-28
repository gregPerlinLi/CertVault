package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.utils.AuthUtils;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Create User DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CreateUserDTO}
 * @date 2025/3/19 19:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CreateUserDTO {

    private String username;

    private String displayName;

    private String email;

    private String password;

    private Integer role;

    public User toUser() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setUsername(this.username);
        user.setDisplayName(this.displayName);
        user.setEmail(this.email);
        if ( this.password != null ) {
            user.setPassword(AuthUtils.encryptPassword(this.password));
        }
        user.setRole(this.role);
        user.setCreatedAt(now);
        user.setModifiedAt(now);
        user.setDeleted(false);
        return user;
    }

    public User updateUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setDisplayName(this.displayName);
        user.setEmail(this.email);
        user.setPassword(AuthUtils.encryptPassword(this.password));
        user.setRole(this.role);
        user.setCreatedAt(now);
        user.setModifiedAt(now);
        user.setDeleted(false);
        return user;
    }
}
