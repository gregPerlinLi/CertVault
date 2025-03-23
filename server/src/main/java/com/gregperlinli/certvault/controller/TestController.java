package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.GeneralConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.TestDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ITestService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Testing
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code TestController}
 * @date 2025/3/10 16:19
 */
@RestController
@RequestMapping
public class TestController {

    @Resource
    private ITestService testService;

    /**
     * GET 测试接口
     *
     * @return 测试结果
     */
    @GetMapping(value = {"/api/", "/api"})
    public ResultVO<TestDTO> testGet() {
        TestDTO testDTO = testService.testGet();
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success getting", testDTO);
    }

    /**
     * POST 测试接口
     *
     * @return 测试结果
     */
    @PostMapping(value = {"/api/", "/api"})
    public ResultVO<TestDTO> testPost() {
        TestDTO testDTO = testService.testPost();
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success posting", testDTO);
    }

    /**
     * PUT 测试接口
     *
     * @return 测试结果
     */
    @PutMapping(value = {"/api/", "/api"})
    public ResultVO<TestDTO> testPut() {
        TestDTO testDTO = testService.testPut();
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success putting", testDTO);
    }

    /**
     * DELETE 测试接口
     *
     * @return 测试结果
     */
    @DeleteMapping(value = {"/api/", "/api"})
    public ResultVO<TestDTO> testDelete() {
        TestDTO testDTO = testService.testDelete();
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success deleting", testDTO);
    }

    /**
     * PATCH 测试接口
     *
     * @return 测试结果
     */
    @PatchMapping(value = {"/api/", "/api"})
    public ResultVO<TestDTO> testPatch() {
        TestDTO testDTO = testService.testPatch();
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "success patching",
                testDTO);
    }

    /**
     * BCrypt 加密测试接口
     *
     * @param password 需要加密的密码
     * @param response 响应对象
     * @return 加密结果
     */
    @GetMapping(value = "/api/v1/test/bcrypt/{password}")
    public ResultVO<String> testBCrypt(@PathVariable("password") String password,
                                       HttpServletResponse response) {
        String result = testService.getBCryptedPassword(password);
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Request success",
                    result);
        }
        response.setHeader(GeneralConstant.STATUS_CODE.getValue(),
                String.valueOf(ResultStatusCodeConstant.FAILED.getResultCode()));
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Request failed");
    }

}
