package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter Can Not be Null API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className ParamNotNullApiResponse
 * @date 2025/4/4 16:35
 */
@ApiResponse(
        responseCode = "422",
        description = "Parameter Can Not be Null",
        content = @Content(
                examples = {@ExampleObject(value = """
                        {
                            "code": 422,
                            "msg": "Parameter cannot be null.",
                            "data": null,
                            "timestamp": "2025-04-04T16:16:02.5641+08:00"
                        }
                        """)}
        )
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamNotNullApiResponse {

}
