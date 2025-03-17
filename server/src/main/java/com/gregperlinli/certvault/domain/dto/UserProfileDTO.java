package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import lombok.*;

import java.util.List;

/**
 * User Profile DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UserProfileDTO}
 * @date 2025/3/10 20:22
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UserProfileDTO {

    /**
     * username
     */
    private String username;

    /**
     * display name
     */
    private String displayName;

    /**
     * email
     */
    private String email;

    /**
     * roles
     */
    private Integer role;

    public UserProfileDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

}
