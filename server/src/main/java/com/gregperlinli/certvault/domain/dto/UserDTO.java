package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.Role;
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
public class UserDTO {

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private List<String> roles;

    public UserDTO(User user, List<Role> roles) {
        this.username = user.getUsername();
        this.roles = roles.stream().map(Role::getRoleName).toList();
    }
}
