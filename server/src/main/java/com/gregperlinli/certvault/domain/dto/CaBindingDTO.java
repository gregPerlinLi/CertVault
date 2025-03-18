package com.gregperlinli.certvault.domain.dto;

import lombok.*;

/**
 * CA User Binding DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaBindingDTO}
 * @date 2025/3/18 11:12
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CaBindingDTO {

    /**
     * CA UUID
     */
    private String caUuid;

    /**
     * User name
     */
    private String username;
}
