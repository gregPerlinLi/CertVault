package com.gregperlinli.certvault.constant;

import lombok.Getter;

/**
 * Redis 常用 Key 前缀枚举
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className RedisKeyConstant
 * @date 2024/1/31 17:30
 */
@Getter
public enum RedisKeyConstant {
    /**
     * 存放已登录用户的key值
     */
    USER("user:", "用户");

    final String keyPrefix;
    final String keyPrefixName;

    RedisKeyConstant(String keyPrefix, String keyPrefixName)
    {
        this.keyPrefix = keyPrefix;
        this.keyPrefixName = keyPrefixName;
    }

    /**
     * 拼接前缀
     *
     * @param key 需要拼接的key
     * @return 拼接后的key
     */
    public String joinLoginPrefix(String key)
    {
        return keyPrefix + key;
    }
}
