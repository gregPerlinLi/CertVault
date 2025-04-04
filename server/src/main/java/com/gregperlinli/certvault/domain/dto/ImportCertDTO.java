package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Import Certificate DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ImportCertDTO}
 * @date 2025/3/25 17:01
 */
@Schema(
        name = "Import Certificate DTO",
        description = "Data transfer object for importing certificates with private key"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ImportCertDTO {

    @Schema(
            name = "privkey",
            description = "Private key of the certificate",
            example = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUpRd0lCQURBTkJn...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Base64"
    )
    private String privkey;

    @Schema(
            name = "certificate",
            description = "Certificate of the certificate",
            example = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUdFekNDQ...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Base64"
    )
    private String certificate;

    @Schema(
            name = "comment",
            description = "Comment of the certificate",
            example = "Testing CA",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String comment;

}
