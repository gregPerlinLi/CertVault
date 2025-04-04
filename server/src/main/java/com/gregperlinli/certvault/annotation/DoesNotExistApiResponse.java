package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                examples = {@ExampleObject(value = """
                        {
                            "code": 404,
                            "msg": "The xxx does not exist.",
                            "data": null,
                            "timestamp": "2025-04-04T15:56:39.159625764+08:00"
                        }
                        """)}
        )
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoesNotExistApiResponse {
}
