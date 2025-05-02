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
 * No Static Resource API Result
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className NoStaticResourceApiResponse
 * @date 2025/4/4 15:57
 */
@ApiResponse(
        responseCode = "404",
        description = "No Static Resource",
        content = @Content(
                schema = @Schema(implementation = ResultVO.NullResult.class),
                examples = {@ExampleObject(value =
                        """
                        {
                            "code": 404,
                            "msg": "No static resource xxx.",
                            "data": null,
                            "timestamp": "2025-04-04T15:56:39+08:00"
                        }
                        """
                )}
        )
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoStaticResourceApiResponse {
}
