package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "User Profile DTO",
        description = "User profile information transfer object containing account details"
)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class UserProfileDTO {

    /**
     * username
     */
    @Schema(
            name = "username",
            description = "Username of the user",
            example = "john.doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String username;

    /**
     * display name
     */
    @Schema(
            name = "displayName",
            description = "Display name of the user",
            example = "John Doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String displayName;

    /**
     * email
     */
    @Schema(
            name = "email",
            description = "Email of the user",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String email;

    /**
     * roles (1: User, 2: Admin, 3: Superadmin)
     */
    @Schema(
            name = "role",
            description = "Role of the user (1: User, 2: Admin, 3: Superadmin)",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Integer"
    )
    private Integer role;

    public UserProfileDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

}
