package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Update User Role DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UpdateRoleDTO}
 * @date 2025/3/19 21:53
 */
@Schema(
        name = "Update Role DTO",
        description = "Data transfer object for updating a user's role information",
        example = """
        {
            "username": "john.doe",
            "role": 2
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateRoleDTO {

    /**
     * Username of the user whose role information needs to be updated
     */
    @Schema(
            name = "Username",
            description = "Username of the user whose role information needs to be updated",
            example = "john.doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String username;

    /**
     * Role of the user (1: User, 2: Admin, 3: Superadmin)
     */
    @Schema(
            name = "Role",
            description = "Role of the user (1: User, 2: Admin, 3: Superadmin)",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Integer"
    )
    private Integer role;

}
