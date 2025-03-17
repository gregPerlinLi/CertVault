package com.gregperlinli.certvault.domain.entities;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Generic Certificate Generate Response DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaGenResponseDTO}
 * @date 2025/3/15 11:52
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class GenResponse {

    /**
     * UUID
     */
    private String uuid;

    /**
     * Private key with BASE64 encoding
     */
    private String privkey;

    /**
     * Certificate with BASE64 encoding
     */
    private String cert;

    /**
     * Effective Date
     */
    private LocalDateTime notBefore;

    /**
     * Expiration Date
     */
    private LocalDateTime notAfter;

    /**
     * Comment
     */
    private String comment;

}
