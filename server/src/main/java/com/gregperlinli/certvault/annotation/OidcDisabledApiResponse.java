package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenID Connect Disabled API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className OidcDiasbledApiResponse
 * @date 2025/4/4 23:14
 */
@ApiResponse(
        responseCode = "204",
        description = "OIDC Disabled",
        content = @Content(
                examples = {@ExampleObject(value =
                        """
                        {
                            "code": 204,
                            "msg": "OIDC Disabled",
                            "data": null,
                            "timestamp": "2025-03-29T00:59:00.06971"
                        }
                        """
                )}
        )
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OidcDisabledApiResponse {
}
