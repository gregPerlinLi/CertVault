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
 * No Valid Session API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className NoValidSessionApiResponse
 * @date 2025/4/4 20:41
 */
@ApiResponse(
        responseCode = "401",
        description = "No valid session found.",
        content = @Content(
                schema = @Schema(implementation = ResultVO.NullResult.class),
                examples = {@ExampleObject(value =
                        """
                        {
                            "code": 401,
                            "msg": "No valid session found.",
                            "data": null,
                            "timestamp": "2025-04-04T16:16:02+08:00"
                        }
                        """
                )}
        )
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoValidSessionApiResponse {

}
