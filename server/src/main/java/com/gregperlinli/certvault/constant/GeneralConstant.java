package com.gregperlinli.certvault.constant;

import lombok.Getter;

/**
 * 通用常量
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className GeneralConstant
 * @date 2024/2/26 15:24
 */
@Getter
public enum GeneralConstant {

    /**
     * 新建账户初始密码
     */
    INITIAL_PASSWORD("cert_v1234", "Initial Password"),
    /**
     * 响应状态码 Header
     */
    STATUS_CODE("Status-Code","Status Code Header");


    final String value;

    final String name;

    GeneralConstant(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
