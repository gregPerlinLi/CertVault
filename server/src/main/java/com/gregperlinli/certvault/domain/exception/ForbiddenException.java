package com.gregperlinli.certvault.domain.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 拒绝异常
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className LoginException
 * @date 2024/1/31 16:46
 */
@Setter
@Getter
@NoArgsConstructor
public class ForbiddenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public ForbiddenException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ForbiddenException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }
}
