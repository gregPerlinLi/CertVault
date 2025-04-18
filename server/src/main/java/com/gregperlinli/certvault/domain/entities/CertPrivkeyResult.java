package com.gregperlinli.certvault.domain.entities;

import com.gregperlinli.certvault.domain.dto.CertPrivkeyDTO;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * PEM Format Certificate and Private Key Entity
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertPrivkeyResult}
 * @date 2025/4/15 00:47
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CertPrivkeyResult {

    /**
     * Certificate Base64
     */
    private String certBase64;

    /**
     * Private Key Base64
     */
    private String privateKeyBase64;

}
