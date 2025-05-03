package com.gregperlinli.certvault.annotation;

import com.gregperlinli.certvault.domain.vo.ResultVO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Insufficient Privileges API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code InsufficientPrivilegesApiResponse}
 * @date 2025/4/4 20:43
 */
@ApiResponse(
        responseCode = "403",
        description = "Insufficient privileges",
        content = @Content(
                schema = @Schema(implementation = ResultVO.NullResult.class),
                examples = {@ExampleObject(value =
                        """
                        {
                            "code": 403,
                            "msg": "Insufficient privileges.",
                            "data": null,
                            "timestamp": "2025-04-04T16:16:02+08:00"
                        }
                        """
                )}
        )
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InsufficientPrivilegesApiResponse {
}
