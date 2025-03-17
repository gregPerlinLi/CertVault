package com.gregperlinli.certvault.constant;

import lombok.Getter;

/**
 * 返回值常量枚举
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className ResultStatusCodeConstant
 * @date 2024/1/31 16:49
 */

@Getter
public enum ResultStatusCodeConstant {
    /**
     * 请求成功
     */
    SUCCESS(200, "Success"),
    /**
     * 处理结果为空
     */
    NOT_FIND(204, "Not find"),
    /**
     * 未登录
     */
    UNAUTHORIZED(401, "Unauthorized"),
    /**
     * 页面未找到
     */
    PAGE_NOT_FIND(404, "Page not find"),
    /**
     * 方法不支持
     */
    METHOD_NOT_ALLOWED(405, "method not allow"),
    /**
     * 无权限访问
     */
    FORBIDDEN(403, "Forbidden"),
    /**
     * 状态异常
     */
    STATUS_EXCEPTION(417, "Status exception"),
    /**
     * 请求失败
     */
    FAILED(444, "Failed"),
    /**
     * 参数校验异常 (参数不合法)
     */
    PARAM_VALIDATE_EXCEPTION(422, "Param validate exception"),
    /**
     * 处理过程报错
     */
    SERVER_ERROR(500, "Server error"),
    /**
     * 业务异常
     */
    BUSINESS_EXCEPTION(501, "Business exception");

    final int resultCode;

    final String resultInfo;

    ResultStatusCodeConstant(int resultCode, String resultInfo) {
        this.resultCode = resultCode;
        this.resultInfo = resultInfo;
    }

}
