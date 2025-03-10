package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.UpdateUserProfileDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.entities.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
public interface IUserService extends IService<User> {

    /**
     * Login
     *
     * @param username username
     * @param password password
     * @param sessionId sessionId
     * @return login result
     */
    UserProfileDTO login(String username, String password, String sessionId);

    /**
     * 登录校验
     *
     * @param username 用户名
     * @param sessionId 登录会话ID
     * @return 校验结果
     */
    boolean loginVerify(String username, String sessionId);

    /**
     * 注销账户
     *
     * @param sessionId 登录会话ID
     */
    void logout(String sessionId);

    /**
     * 获取自己的用户信息
     *
     * @param username 用户名
     */
    UserProfileDTO getOwnProfile(String username);

    /**
     * 更新自己的用户信息
     *
     * @param username 用户名
     * @param updateUserProfileDTO 更新用户信息DTO
     */
    Boolean updateOwnProfile(String username, UpdateUserProfileDTO updateUserProfileDTO);

}
