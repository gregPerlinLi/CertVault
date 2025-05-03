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
 * Already Exist API Response
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className ParamNotNullApiResponse
 * @date 2025/4/4 16:35
 */
@ApiResponse(
        responseCode = "422",
        description = "Resource Already Exist",
        content = @Content(
                schema = @Schema(implementation = ResultVO.NullResult.class),
                examples = {@ExampleObject(value =
                        """
                        {
                            "code": 422,
                            "msg": "The xxx already exist.",
                            "data": null,
                            "timestamp": "2025-04-04T16:16:02+08:00"
                        }
                        """
                )}
        )
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AlreadyExistApiResponse {

}
