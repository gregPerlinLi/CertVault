package com.gregperlinli.certvault.domain.dto;

import lombok.*;

/**
 * Request Private Key DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code RequestPrivKeyDTO}
 * @date 2025/3/17 23:09
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class RequestPrivkeyDTO {

    /**
     * Request CA/SSL Private Key's UUID
     */
    private String uuid;

    /**
     * Request User Password
     */
    private String password;

}
