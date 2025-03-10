package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.TestDTO;

/**
 * Testing
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className ITestService
 * @date 2025/3/10 16:21
 */
public interface ITestService {

    /**
     * 测试接口
     *
     * @return 测试结果
     */
    TestDTO testGet();

    /**
     * 测试接口
     *
     * @return 测试结果
     */
    TestDTO testPost();

    /**
     * 测试接口
     *
     * @return 测试结果
     */
    TestDTO testPut();

    /**
     * 测试接口
     *
     * @return 测试结果
     */
    TestDTO testDelete();

    /**
     * 测试接口
     *
     * @return 测试结果
     */
    TestDTO testPatch();

    /**
     * 获取 BCrypt 加密后的密文
     *
     * @param password 明文密码
     * @return
     */
    String getBCryptedPassword(String password);
}
