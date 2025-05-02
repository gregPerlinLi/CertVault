package com.gregperlinli.certvault.domain.vo;

import com.gregperlinli.certvault.domain.dto.OidcProviderDTO;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.dto.ResponseCertDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Universal Result Object
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ResultVO}
 * @date 2025/3/3 16:34
 */
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> {

    /**
     * 消息代码
     */
    @Schema(
            name = "code",
            description = "Message code",
            examples = {
                    "200", "204", "302", "400", "401", "403", "404", "405", "417", "422", "444", "500", "501"
            },
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "integer"
    )
    private Integer code;

    /**
     * 返回信息
     */
    @Schema(
            name = "msg",
            description = "Return message",
            example = "Success",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String msg;

    /**
     * 返回前端需要获取的实体对象
     */
    @Schema(
            name = "data",
            description = "Data",
            type = "T",
            nullable = true
    )
    private T data;

    /**
     * 时间戳
     */
    @Schema(
            name = "timestamp",
            description = "Timestamp",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "date-time"
    )
    private String timestamp;

    public static class UserProfileResult extends ResultVO<UserProfileDTO> {}

    public static class CertResponseResult extends ResultVO<ResponseCertDTO> {}

    public static class OidcProviderListResult extends ResultVO<List<OidcProviderDTO>> {}

    public static class NullResult extends ResultVO<Void> {}

    public static class NullPageResult extends ResultVO<PageDTO<Void>> {}

    /**
     * 返回错误结果方法
     *
     * @param code 错误代码
     * @param message 错误信息
     */
    public ResultVO(Integer code, String message) {
        this(code, message, null, OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    /**
     * 返回正确结果方法
     *
     * @param code 状态码
     * @param message 信息
     * @param data 返回数据
     */
    public ResultVO(Integer code, String message, T data) {
        this(code, message, data, OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

}
