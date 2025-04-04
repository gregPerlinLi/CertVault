package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Success and Failed API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className SuccessApiResponse
 * @date 2025/4/4 16:40
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuccessApiResponse
@FailedApiResponse
public @interface SuccessAndFailedApiResponse {

}
