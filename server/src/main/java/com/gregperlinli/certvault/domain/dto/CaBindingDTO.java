package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * CA User Binding DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaBindingDTO}
 * @date 2025/3/18 11:12
 */
@Schema(
        name = "CA Binding DTO",
        description = "CA-User binding data transfer object"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CaBindingDTO {

    /**
     * CA UUID
     */
    @Schema(
            name = "caUuid",
            description = "The UUID of the Certificate Authority (CA)",
            example = "2f2d63a8-b29c-4404-ae10-81f5ff023a69",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String caUuid;

    /**
     * User name
     */
    @Schema(
            name = "username",
            description = "The username of the user associated with the CA binding",
            example = "john.doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String username;
}
