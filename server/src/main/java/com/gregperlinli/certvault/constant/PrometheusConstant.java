package com.gregperlinli.certvault.constant;

import lombok.Getter;

/**
 * Prometheus 相关常量
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className CommonConstant
 * @date 2024/2/21 10:17
 */
@Getter
public enum PrometheusConstant {

    /**
     * Prometheus指标项名称
     */
    PROMETHEUS_METRICS("api.request.count", "Prometheus指标项名称"),

    /**
     * API请求码
     */
    API_CODE("apiCode", "API请求码"),

    /**
     * 所属API控制器类
     */
    CONTROLLER("controller", "所属API控制器类"),

    /**
     * API路径
     */
    PATH("path", "API路径"),

    /**
     * API请求方法
     */
    METHOD("method", "API请求方法"),

    /**
     * API请求结果码
     */
    CODE("code", "API请求结果码");



    final String name;

    final String description;

    PrometheusConstant(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
