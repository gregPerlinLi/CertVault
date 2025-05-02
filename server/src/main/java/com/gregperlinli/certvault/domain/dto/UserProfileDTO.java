package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
            type = "string"
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
            type = "string"
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
            type = "string"
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
            type = "integer"
    )
    private Integer role;

    /**
     * Whether the user has initialized their password
     */
    @Schema(
            name = "isPasswordInitialized",
            description = "Whether the user has initialized their password",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "boolean"
    )
    private Boolean isPasswordInitialized = true;

    public UserProfileDTO(String testUser, String testDisplayName, String mail, int role) {
        this.username = testUser;
        this.displayName = testDisplayName;
        this.email = mail;
        this.role = role;
    }

    public UserProfileDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.role = user.getRole();
        if ( user.getPassword() == null || user.getPassword().isEmpty() ) {
            this.isPasswordInitialized = false;
        }
    }

}
