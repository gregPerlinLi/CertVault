package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Page data transmission entity
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code PageDTO}
 * @date 2024/1/31 19:55
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class PageDTO<T> {

    /**
     * Total
     */
    @Schema(
            name = "total",
            description = "Total",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Long",
            minLength = 1,
            maxLength = 3
    )
    private Long total;

    /**
     * Current page
     */
    private List<T> list;
}
