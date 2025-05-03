package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "Test DTO",
        description = "Data transfer object for testing API responses and system status"
)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class TestDTO implements Serializable {

    @Schema(
            name = "method",
            description = "HTTP method used for the request",
            example = "GET",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String method;

    @Schema(
            name = "message",
            description = "Response message",
            example = "success getting",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String message;

    @Schema(
            name = "version",
            description = "Version of the Server",
            example = "1.1.3",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String version;

    @Schema(
            name = "serverTimezone",
            description = "Timezone of the Server",
            example = "+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String serverTimezone;

}
