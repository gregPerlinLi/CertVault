package com.gregperlinli.certvault.domain.dto;

import lombok.*;

import java.io.Serializable;

/**
 * Testing DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code TestDTO}
 * @date 2024/2/2 17:34
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class TestDTO implements Serializable {

    private String method;

    private String message;

    private String version;

    private String serverTimezone;

}
