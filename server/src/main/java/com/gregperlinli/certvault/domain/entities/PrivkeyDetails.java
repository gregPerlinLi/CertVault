package com.gregperlinli.certvault.domain.entities;

import lombok.*;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Private Key Details
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code PrivkeyDetails}
 * @date 2025/4/21 11:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class PrivkeyDetails {

    // Common parameters

    private String algorithm;
    private String encoded;
    private String format;
    private AlgorithmParameterSpec params;
    private PrivkeySpecific privkeySpecific;

}
