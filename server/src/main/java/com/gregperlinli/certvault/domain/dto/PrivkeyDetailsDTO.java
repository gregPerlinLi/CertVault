package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.PrivkeyDetails;
import com.gregperlinli.certvault.domain.entities.PrivkeySpecific;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.security.spec.AlgorithmParameterSpec;

/**
 * Private Key Details DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code PrivkeyDetailsDTO}
 * @date 2025/4/21 14:20
 */
@Schema(
        name = "Private Key Details DTO",
        description = "Private Key details transfer object containing metadata and cryptographic information"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class PrivkeyDetailsDTO {

    /**
     * Algorithm of the private key
     */
    @Schema(
            name = "algorithm",
            description = "Algorithm of the private key",
            example = "RSA",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String algorithm;

    /**
     * Encoded of the private key
     */
    @Schema(
            name = "encoded",
            description = "Encoded of the private key",
            example = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1GAQWvotGCPu1QAB14hzKF5C2bc9WRecF...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String encoded;

    /**
     * Format of the private key
     */
    @Schema(
            name = "format",
            description = "Format of the private key",
            example = "PKCS#8",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String format;

    /**
     * Algorithm Parameter Spec of the private key
     */
    @Schema(
            name = "params",
            description = "Algorithm Parameter Spec of the private key",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "AlgorithmParameterSpec"
    )
    private AlgorithmParameterSpec params;

    /**
     * Private Key Specific
     */
    @Schema(
            name = "privkey",
            description = "Private Key Specific",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "PrivkeySpecific",
            example = """
                    {
                        "modulus": "228609759160589102334607729602696527515026450133506517096342257090026912981548985018...",
                        "prime": {
                            "p": "153760461261178965010117263539283481275456885011494138046055221948725869202108767925782429...",
                            "q": "148679158013366268620605192719915250915997392432944427360405967071016077019192990038738873...",
                            "exponentP": "15860059479768219531995555205846412460474061105599895832756050786172496083224060777549921...",
                            "exponentQ": "70091570654454189775275310653134894674773874858602789687155389453684372424508088869445904..."
                        },
                        "privateExponent": "3299540277858327843787562009722609295153325895010205143986299967674084211807790788851983...",
                        "publicExponent": "65537",
                        "coefficient": "132134676487591500472106038450127896519064821839151815733859424292775374431869116103238791..."
                    }
                    """
    )
    private PrivkeySpecific privkey;

    public PrivkeyDetailsDTO(PrivkeyDetails privkeyDetails) {
        this.algorithm = privkeyDetails.getAlgorithm();
        this.encoded = privkeyDetails.getEncoded();
        this.format = privkeyDetails.getFormat();
        this.params = privkeyDetails.getParams();
        this.privkey = privkeyDetails.getPrivkeySpecific();
    }

}
