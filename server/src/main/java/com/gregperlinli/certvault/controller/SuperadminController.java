package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.CreateUserDTO;
import com.gregperlinli.certvault.domain.dto.UpdateRoleDTO;
import com.gregperlinli.certvault.domain.dto.UpdateUserProfileDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Superadmin Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SuperadminController}
 * @date 2025/3/19 19:39
 */
@RequestMapping("/api/superadmin")
@RestController
public class SuperadminController {

    @Resource
    IUserService userService;

    /**
     * Create a new user
     *
     * @param createUserDTO the user information
     * @return created user
     * @throws Exception if the user already exists or the parameters of the entity is null
     */
    @PostMapping(value = "/user")
    public ResultVO<UserProfileDTO> createUser(@RequestBody CreateUserDTO createUserDTO) throws Exception {
        UserProfileDTO userProfileDTO = userService.createUser(createUserDTO);
        if ( userProfileDTO != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "create success", userProfileDTO);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "create failed");
    }

    /**
     * Create multiple users
     *
     * @param createUserDTOs the user information
     * @return created result
     * @throws Exception if the user already exists or the parameters of the entity is null
     */
    @PostMapping(value = "/users")
    public ResultVO<Void> createUsers(@RequestBody List<CreateUserDTO> createUserDTOs) throws Exception {
        Boolean result = userService.createUsers(createUserDTOs);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "create success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "create failed");
    }

    /**
     * Update user information
     *
     * @param username the username of the user to be updated
     * @param updateUserProfileDTO the user information
     * @return updated result
     */
    @PutMapping(value = "/user/{username}")
    public ResultVO<Void> updateUserInfo(@PathVariable("username") String username,
                                         @RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        Boolean result = userService.updateUserProfile(username, updateUserProfileDTO, true);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "update success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "update failed");
    }

    /**
     * Update user role
     *
     * @param updateRoleDTO the user information
     * @param request the request object
     * @return updated result
     * @throws Exception if the user already exists or the parameters of the entity is null
     */
    @PatchMapping(value = "/user/role")
    public ResultVO<Void> updateUserRole(@RequestBody UpdateRoleDTO updateRoleDTO,
                                         HttpServletRequest request) throws Exception {
        Boolean result = userService.updateUserRole(updateRoleDTO, request.getSession().getAttribute("username").toString());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "update success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "update failed");
    }

    /**
     * Update multiple users role
     *
     * @param updateRoleDTOs the user information
     * @param request the request object
     * @return updated result
     * @throws Exception if the user already exists or the parameters of the entity is null
     */
    @PatchMapping(value = "/users/role")
    public ResultVO<Void> updateUsersRole(@RequestBody List<UpdateRoleDTO> updateRoleDTOs,
                                          HttpServletRequest request) throws Exception {
        Boolean result = userService.updateUsersRole(updateRoleDTOs, request.getSession().getAttribute("username").toString());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "update success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "update failed");
    }

    /**
     * Delete user
     *
     * @param username the username of the user to be deleted
     * @param request the request object
     * @return deleted result
     */
    @DeleteMapping(value = "/user/{username}")
    public ResultVO<Void> deleteUser(@PathVariable("username") String username,
                                     HttpServletRequest request) {
        Boolean result = userService.deleteUser(username, request.getSession().getAttribute("username").toString());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "delete success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "delete failed");
    }

    /**
     * Delete multiple users
     *
     * @param usernames the usernames of the users to be deleted
     * @param request the request object
     * @return deleted result
     */
    @DeleteMapping(value = "/users")
    public ResultVO<Void> deleteUsers(@RequestBody List<String> usernames,
                                      HttpServletRequest request) {
        Boolean result = userService.deleteUsers(usernames, request.getSession().getAttribute("username").toString());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "delete success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "delete failed");
    }

}
