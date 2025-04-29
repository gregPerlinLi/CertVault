package com.gregperlinli.certvault.domain.entities;

import lombok.*;

import java.math.BigInteger;

/**
 * Ed25519 Private Key Details
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code Ed25519PrivkeySpecific}
 * @date 2025/4/21 14:09
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Ed25519PrivkeySpecific implements PrivkeySpecific {

    // Ed25519 proprietary parameters

    private Point point;
    private String pointEncoding;

    @Data
    public static class Point {
        private BigInteger x;
        private Boolean yodd;
    }

}
