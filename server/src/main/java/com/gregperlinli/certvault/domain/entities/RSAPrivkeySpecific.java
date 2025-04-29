package com.gregperlinli.certvault.domain.entities;

import lombok.*;

import java.math.BigInteger;

/**
 * RSA Private Key Details
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code RSAPrivkeySpecific}
 * @date 2025/4/21 14:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class RSAPrivkeySpecific implements PrivkeySpecific {

    private BigInteger modulus;
    private Prime prime;
    private BigInteger privateExponent;
    private BigInteger publicExponent;
    private BigInteger coefficient;

    @Data
    public static class Prime {
        private BigInteger p;
        private BigInteger q;
        private BigInteger exponentP;
        private BigInteger exponentQ;
    }

}
