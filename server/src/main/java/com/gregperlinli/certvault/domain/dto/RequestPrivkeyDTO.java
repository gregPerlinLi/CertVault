package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Request Private Key DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code RequestPrivKeyDTO}
 * @date 2025/3/17 23:09
 */
@Schema(
        name = "Request Private Key DTO",
        description = "Data transfer object for requesting a private key with authentication",
        example = """
        {
            "uuid": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            "password": "secureAccess123"
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class RequestPrivkeyDTO {

    /**
     * Request CA/SSL Private Key's UUID
     */
    @Schema(
            name = "UUID",
            description = "UUID of the CA/SSL Private Key",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String uuid;

    /**
     * Request User Password
     */
    @Schema(
            name = "Password",
            description = "Password of the User",
            example = "secureAccess123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String password;

}
