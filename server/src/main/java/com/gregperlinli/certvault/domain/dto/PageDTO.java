package com.gregperlinli.certvault.domain.dto;

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
    private Long total;

    /**
     * Current page
     */
    private List<T> list;
}
