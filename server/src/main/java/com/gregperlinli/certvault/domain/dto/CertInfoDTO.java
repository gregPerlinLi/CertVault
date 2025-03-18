package com.gregperlinli.certvault.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Certificate Info DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertInfoDTO}
 * @date 2025/3/18 20:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CertInfoDTO {

    private String uuid;

    private String caUuid;

    private String owner;

    private String comment;

    private LocalDateTime notBefore;

    private LocalDateTime notAfter;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
