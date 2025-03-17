package com.gregperlinli.certvault.domain.dto;

import lombok.*;

/**
 * Update Certificate Comment DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UpdateCommentDTO}
 * @date 2025/3/17 23:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateCommentDTO {

    /**
     * Certificate UUID
     */
    private String uuid;

    /**
     * Certificate Comment
     */
    private String comment;

}
