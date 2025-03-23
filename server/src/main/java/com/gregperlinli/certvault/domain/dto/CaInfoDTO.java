package com.gregperlinli.certvault.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * CA Information DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaInfoDTO}
 * @date 2025/3/17 22:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CaInfoDTO {

    private String uuid;

    private String owner;

    private String parentCa;

    private Boolean allowSubCa;

    private String comment;

    private Boolean available;

    private LocalDateTime notBefore;

    private LocalDateTime notAfter;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
