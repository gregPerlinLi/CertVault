package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.entities.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * Create user
     *
     * @param createUserDTO create user DTO
     * @return user profile
     * @throws Exception if the parameter of the entity is null or ths user is already exists
     */
    UserProfileDTO createUser(CreateUserDTO createUserDTO) throws Exception;

    /**
     * Create multiple users
     *
     * @param createUserDTOs create user DTOs
     * @return create result
     * @throws Exception if the parameter of the entity is null or ths user is already exists
     */
    Boolean createUsers(List<CreateUserDTO> createUserDTOs) throws Exception;

    /**
     * Update user role
     *
     * @param updateRoleDTO update role DTO
     * @param requestUser request user
     * @return update result
     * @throws Exception if the parameter of the entity is null or ths user is already exists
     */
    Boolean updateUserRole(UpdateRoleDTO updateRoleDTO, String requestUser) throws Exception;

    /**
     * Update multiple users role
     *
     * @param updateRoleDTOs update role DTOs
     * @param requestUser request user
     * @return update result
     * @throws Exception if the parameter of the entity is null or ths user is already exists
     */
    Boolean updateUsersRole(List<UpdateRoleDTO> updateRoleDTOs, String requestUser) throws Exception;

    /**
     * Delete user
     *
     * @param username    username
     * @param requestUser request user
     * @return delete result
     */
    Boolean deleteUser(String username, String requestUser);

    /**
     * Delete multiple users
     *
     * @param usernames   usernames
     * @param requestUser request user
     * @return delete result
     */
    Boolean deleteUsers(List<String> usernames, String requestUser);

}
