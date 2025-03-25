package com.gregperlinli.certvault.domain.dto;

import lombok.*;

/**
 * Import Certificate DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ImportCertDTO}
 * @date 2025/3/25 17:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ImportCertDTO {

    private String privkey;

    private String certificate;

    private String comment;

}
