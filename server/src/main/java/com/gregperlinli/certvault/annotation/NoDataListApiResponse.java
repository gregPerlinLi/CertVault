package com.gregperlinli.certvault.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * No Data API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className NoDataApiResponse
 * @date 2025/4/4 15:48
 */
@ApiResponse(
        responseCode = "204",
        description = "No Data",
        content = @Content(
                examples = {@ExampleObject(value = """
                        {
                            "code": 204,
                            "msg": "No data",
                            "data": {
                                "total": 0,
                                "list": []
                            },
                            "timestamp": "2025-04-04T15:46:18.96845051+08:00"
                        }
                        """)
                })
)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDataListApiResponse {


}
