package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Login data transfer object
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code LoginDTO}
 * @date 2025/3/3 22:39
 */
@Schema(
        name = "Login DTO",
        description = "Data transfer object for user authentication"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class LoginDTO {

    /**
     * username
     */
    @Schema(
            name = "username",
            description = "Username of the user",
            example = "testadmin",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String username;

    /**
     * password
     */
    @Schema(
            name = "password",
            description = "Password of the user",
            example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String password;

    public LoginDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

}
