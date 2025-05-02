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
        description = "Data transfer object for updating user profile information"
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
            name = "displayName",
            description = "Display name of the user",
            example = "John Doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String displayName;

    /**
     * Email
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
     * Old password
     */
    @Schema(
            name = "oldPassword",
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
            name = "newPassword",
            description = "New password of the user",
            example = "newPass456",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String newPassword;

}
