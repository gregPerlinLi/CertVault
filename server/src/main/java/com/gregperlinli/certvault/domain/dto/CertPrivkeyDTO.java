package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.CertPrivkeyResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Certificate and Private Key DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertPrivkeyDTO}
 * @date 2025/4/17 22:21
 */
@Schema(
        name = "Certificate and Private Key DTO",
        description = "Certificate and Private Key Information"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CertPrivkeyDTO {

    /**
     * Certificate with BASE64 encoding
     */
    @Schema(
            name = "cert",
            description = "Certificate with BASE64 encoding",
            example = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUV2QUl...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String cert;

    /**
     * Private key with BASE64 encoding
     */
    @Schema(
            name = "privkey",
            description = "Private key with BASE64 encoding",
            example = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2QUl...",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "String"
    )
    private String privkey;

    public CertPrivkeyDTO(CertPrivkeyResult certPrivkeyResult) {
        this.cert = certPrivkeyResult.getCertBase64();
        this.privkey = certPrivkeyResult.getPrivateKeyBase64();
    }

}
