package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Not Your Resource API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className NotYourResourceApiResponse
 * @date 2025/4/4 16:16
 */
@ApiResponse(
        responseCode = "403",
        description = "Not Your Resource",
        content = @Content(
                examples = {@ExampleObject(value = """
                        {
                            "code": 403,
                            "msg": "The resource is not yours.",
                            "data": null,
                            "timestamp": "2025-04-04T16:16:02.5641+08:00"
                        }
                        """
                )}
        )
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotYourResourceApiResponse {
}
