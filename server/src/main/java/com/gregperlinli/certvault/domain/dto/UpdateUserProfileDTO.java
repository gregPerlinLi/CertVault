package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Update User Profile DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UpdateProfileDTO}
 * @date 2025/3/10 20:23
 */
@Schema(
        name = "Update User Profile DTO",
        description = "Data transfer object for updating user profile information",
        example = """
        {
            "displayName": "John Doe",
            "email": "john.doe@example.com",
            "oldPassword": "oldPass123",
            "newPassword": "newPass456"
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateUserProfileDTO {

    /**
     * Display name
     */
    @Schema(
            name = "Display Name",
            description = "Display name of the user",
            example = "John Doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String displayName;

    /**
     * Email
     */
    @Schema(
            name = "Email",
            description = "Email of the user",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String email;

    /**
     * Old password
     */
    @Schema(
            name = "Old Password",
            description = "Old password of the user",
            example = "oldPass123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String oldPassword;

    /**
     * New password
     */
    @Schema(
            name = "New Password",
            description = "New password of the user",
            example = "newPass456",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String newPassword;

}
