package com.gregperlinli.certvault.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 证书生成异常
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className CertGenException
 * @date 2024/1/31 16:41
 */
@NoArgsConstructor
@Getter
@Setter
public class CertGenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public CertGenException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public CertGenException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

}
