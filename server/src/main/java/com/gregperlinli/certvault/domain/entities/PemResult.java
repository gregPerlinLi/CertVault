package com.gregperlinli.certvault.domain.entities;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * PEM Format Certificate and Private Key Entity
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code PemResult}
 * @date 2025/4/15 00:47
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class PemResult {

    /**
     * Certificate Base64
     */
    private String certBase64;

    /**
     * Private Key Base64
     */
    private String privateKeyBase64;

}
