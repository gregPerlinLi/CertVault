package com.gregperlinli.certvault.constant;

import lombok.Getter;

/**
 * 账户类型
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className AccountTypeConstant
 * @date 2024/2/2 12:02
 */
@Getter
public enum AccountTypeConstant {
    /**
     * 未知
     */
    UNKNOWN(0, "unknown"),
    /**
     * 普通用户
     */
    USER(1, "user"),
    /**
     * 管理员
     */
    ADMIN(2, "admin"),
    /**
     * 超级管理员
     */
    SUPERADMIN(3, "superadmin");

    final int accountType;

    final String accountTypeName;

    AccountTypeConstant(int accountType, String accountTypeName) {
        this.accountType = accountType;
        this.accountTypeName = accountTypeName;
    }

    /**
     * 判断用户类型是否符合
     *
     * @param accountType 需要判断的用户类型({@link String})
     * @return 返回结果
     */
    public boolean matches(String accountType) {
        return this.accountType == Integer.parseInt(accountType);
    }

    /**
     * 判断用户类型是否符合
     *
     * @param accountType 需要判断的用户类型({@link Integer})
     * @return 返回结果
     */
    public boolean matches(Integer accountType) {
        return this.accountType == accountType;
    }
}
