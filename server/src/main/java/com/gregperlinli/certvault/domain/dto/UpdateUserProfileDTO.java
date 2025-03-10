package com.gregperlinli.certvault.domain.dto;

import lombok.*;

/**
 * Update User Profile DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UpdateProfileDTO}
 * @date 2025/3/10 20:23
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateUserProfileDTO {

    /**
     * Display name
     */
    private String displayName;

    /**
     * Old password
     */
    private String oldPassword;

    /**
     * New password
     */
    private String newPassword;

}
