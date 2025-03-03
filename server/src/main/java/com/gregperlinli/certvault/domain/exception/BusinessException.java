package com.gregperlinli.certvault.domain.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className BusinessException
 * @date 2024/1/31 16:41
 */
@Setter
@Getter
@NoArgsConstructor
public class BusinessException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }
}
