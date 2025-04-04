package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Success API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className SuccessApiResponse
 * @date 2025/4/4 16:40
 */
@ApiResponse(
        responseCode = "200",
        description = "Request Success"
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SuccessApiResponse {
}
