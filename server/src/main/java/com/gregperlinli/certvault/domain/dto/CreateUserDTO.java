package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.utils.AuthUtils;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "Create User DTO",
        description = "Data transfer object for user creation and updates",
        example = """
        {
            "username": "hello",
            "displayName": "Hello Admin",
            "email": "k8qscp.k65@yahoo.com.cn",
            "password": "1234567890",
            "role": 2
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CreateUserDTO {

    @Schema(
            name = "Username",
            description = "Username of the user",
            example = "hello",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String username;

    @Schema(
            name = "Display Name",
            description = "Display name of the user",
            example = "Hello Admin",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String displayName;

    @Schema(
            name = "Email",
            description = "Email address of the user",
            example = "k8qscp.k65@yahoo.com.cn",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String email;

    @Schema(
            name = "Password",
            description = "Password of the user",
            example = "1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String password;

    @Schema(
            name = "Role",
            description = "Role of the user (1: User, 2: Admin, 3: Superadmin)",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Integer"
    )
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
        user.setModifiedAt(now);
        user.setDeleted(false);
        return user;
    }
}
