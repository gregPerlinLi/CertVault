package com.gregperlinli.certvault.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回前端需要获取的实体对象
     */
    private T data;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 返回错误结果方法
     *
     * @param code 错误代码
     * @param message 错误信息
     */
    public ResultVO(Integer code, String message) {
        this(code, message, null, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    /**
     * 返回正确结果方法
     *
     * @param code 状态码
     * @param message 信息
     * @param data 返回数据
     */
    public ResultVO(Integer code, String message, T data) {
        this(code, message, data, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

}
