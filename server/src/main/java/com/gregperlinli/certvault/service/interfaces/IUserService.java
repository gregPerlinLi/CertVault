package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.PageDTO;
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

    //////////////////////////////////////////////////
    //   The following is the general user method   //
    //////////////////////////////////////////////////

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
     * Login Verify
     *
     * @param username username
     * @param sessionId login session ID
     * @return verify result
     */
    boolean loginVerify(String username, String sessionId);

    /**
     * Logout
     *
     * @param sessionId session ID
     */
    void logout(String sessionId);

    /**
     * Get user profile
     *
     * @param username username
     */
    UserProfileDTO getOwnProfile(String username);

    /**
     * update user profile
     *
     * @param username 用户名
     * @param updateUserProfileDTO 更新用户信息DTO
     * @param isSuperadmin 是否为超级管理员
     */
    Boolean updateUserProfile(String username, UpdateUserProfileDTO updateUserProfileDTO, boolean isSuperadmin);

    //////////////////////////////////////////////////
    // The following is the admin/superadmin method //
    //////////////////////////////////////////////////

    /**
     * Get all users
     *
     * @param page page
     * @param limit limit
     * @return user profile list
     */
    PageDTO<UserProfileDTO> getAllUsers(Integer page, Integer limit);



}
