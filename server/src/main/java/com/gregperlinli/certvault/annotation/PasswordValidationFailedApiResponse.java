package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Password Validation Failed API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className PasswordValidationFailedApiResponse
 * @date 2025/4/4 16:12
 */
@ApiResponse(
        responseCode = "444",
        description = "Password validation failed",
        content = @Content(
                examples = {@ExampleObject(value = """
                        {
                            "code": 403,
                            "msg": "Password validation failed",
                            "data": null,
                            "timestamp": "2025-04-04T09:45:34.622698063+08:00"
                        }
                        """)}
        )
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidationFailedApiResponse {

}
