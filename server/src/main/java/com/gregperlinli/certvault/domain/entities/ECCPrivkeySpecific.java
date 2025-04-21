package com.gregperlinli.certvault.domain.entities;

import lombok.*;

import java.math.BigInteger;

/**
 * ECC Private Key Details
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ECCPrivkeySpecific}
 * @date 2025/4/21 14:08
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ECCPrivkeySpecific implements PrivkeySpecific {

    // ECC proprietary parameters

    private String d;
    private String curveName;
    private Q q;

    @Data
    public static class Q {
        private BigInteger x;
        private BigInteger y;
        private Integer coordinateSystem;
    }

}
