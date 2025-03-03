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
    SUCCESS(200, "success"),
    /**
     * 处理结果为空
     */
    NOT_FIND(204, "Not find"),
    /**
     * 未登录
     */
    UNAUTHORIZED(401, "未登录"),
    /**
     * 页面未找到
     */
    PAGE_NOT_FIND(404, "page not find"),
    /**
     * 方法不支持
     */
    METHOD_NOT_ALLOWED(405, "method not allow"),
    /**
     * 无权限访问
     */
    FORBIDDEN(403, "无权限访问"),
    /**
     * 状态异常
     */
    STATUS_EXCEPTION(417, "status exception"),
    /**
     * 请求失败
     */
    FAILED(444, "failed"),
    /**
     * 参数校验异常 (参数不合法)
     */
    PARAM_VALIDATE_EXCEPTION(422, "param validate exception"),
    /**
     * 处理过程报错
     */
    SERVER_ERROR(500, "server error"),
    /**
     * 业务异常
     */
    BUSINESS_EXCEPTION(501, "business exception");

    final int resultCode;

    final String resultInfo;

    ResultStatusCodeConstant(int resultCode, String resultInfo) {
        this.resultCode = resultCode;
        this.resultInfo = resultInfo;
    }

}
