package com.gregperlinli.certvault.domain.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 参数校验异常
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className ParamValidateException
 * @date 2024/1/31 16:47
 */
@Setter
@Getter
@NoArgsConstructor
public class ParamValidateException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public ParamValidateException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ParamValidateException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }
}
