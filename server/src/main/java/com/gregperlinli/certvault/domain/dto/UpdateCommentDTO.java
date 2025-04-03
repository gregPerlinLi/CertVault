package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Update Certificate Comment DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UpdateCommentDTO}
 * @date 2025/3/17 23:36
 */
@Schema(
        name = "Update Comment DTO",
        description = "Data transfer object for updating a certificate's comment field",
        example = """
        {
            "uuid": "99d6cb53-1151-4b2d-a9d1-d1a71d58d47c",
            "comment": "Updated security policy requirements"
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateCommentDTO {

    /**
     * Certificate UUID
     */
    @Schema(
            name = "UUID",
            description = "The UUID of the certificate to update",
            example = "99d6cb53-1151-4b2d-a9d1-d1a71d58d47c",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String uuid;

    /**
     * Certificate Comment
     */
    @Schema(
            name = "Comment",
            description = "The updated comment for the certificate",
            example = "Updated security policy requirements",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String comment;

}
