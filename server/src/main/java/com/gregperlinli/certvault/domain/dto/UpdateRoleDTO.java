package com.gregperlinli.certvault.domain.dto;

import lombok.*;

/**
 * Update User Role DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UpdateRoleDTO}
 * @date 2025/3/19 21:53
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateRoleDTO {

    private String username;

    private Integer role;

}
