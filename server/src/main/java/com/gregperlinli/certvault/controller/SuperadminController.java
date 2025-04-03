package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.CreateUserDTO;
import com.gregperlinli.certvault.domain.dto.UpdateRoleDTO;
import com.gregperlinli.certvault.domain.dto.UpdateUserProfileDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.gregperlinli.certvault.service.interfaces.ICertificateService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Superadmin", description = "Superadmin API")
@RequestMapping("/api/v1/superadmin")
@RestController
public class SuperadminController {

    @Resource
    IUserService userService;

    @Resource
    ICaService caService;

    @Resource
    ICertificateService certificateService;

    /**
     * Create a new user
     *
     * @param createUserDTO the user information
     * @return created user
     * @throws Exception if the user already exists or the parameters of the entity is null
     */
    @Operation(
            summary = "Create user",
            description = "Create a new user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Create Success"),
                    @ApiResponse(responseCode = "444", description = "Create Failed")
            }
    )
    @PostMapping(value = "/user")
    public ResultVO<UserProfileDTO> createUser(@Parameter(name = "CreateUserDTO", description = "Create user entity")
                                                   @RequestBody CreateUserDTO createUserDTO) throws Exception {
        UserProfileDTO userProfileDTO = userService.createUser(createUserDTO);
        if ( userProfileDTO != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "create success",
                    userProfileDTO);
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
    @Operation(
            summary = "Create users",
            description = "Create multiple new users (i.e., import users)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request Success"),
                    @ApiResponse(responseCode = "444", description = "Request Failed")
            }
    )
    @PostMapping(value = "/users")
    public ResultVO<Void> createUsers(@Parameter(name = "CreateUserDTOs", description = "Create user entity list")
                                          @RequestBody List<CreateUserDTO> createUserDTOs) throws Exception {
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
    @Operation(
            summary = "Update user information",
            description = "Update user information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Update Success"),
                    @ApiResponse(responseCode = "444", description = "Update Failed")
            }
    )
    @PatchMapping(value = "/user/{username}")
    public ResultVO<Void> updateUserInfo(@Parameter(name = "username", description = "Username of the user to be updated")
                                             @PathVariable("username") String username,
                                         @Parameter(name = "UpdateUserProfileDTO", description = "Update user entity")
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
    @Operation(
            summary = "Update user role",
            description = "Update user role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Update Success"),
                    @ApiResponse(responseCode = "444", description = "Update Failed")
            }
    )
    @PatchMapping(value = "/user/role")
    public ResultVO<Void> updateUserRole(@Parameter(name = "UpdateRoleDTO", description = "Update user role entity")
                                             @RequestBody UpdateRoleDTO updateRoleDTO,
                                         HttpServletRequest request) throws Exception {
        Boolean result = userService.updateUserRole(updateRoleDTO,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
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
    @Operation(
            summary = "Update users role",
            description = "Batch update user roles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Update Success"),
                    @ApiResponse(responseCode = "444", description = "Update Failed")
            }
    )
    @PatchMapping(value = "/users/role")
    public ResultVO<Void> updateUsersRole(@Parameter(name = "UpdateRoleDTOs", description = "Update user roles entity list")
                                              @RequestBody List<UpdateRoleDTO> updateRoleDTOs,
                                          HttpServletRequest request) throws Exception {
        Boolean result = userService.updateUsersRole(updateRoleDTOs,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
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
    @Operation(
            summary = "Delete user",
            description = "Delete user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Delete Success"),
                    @ApiResponse(responseCode = "444", description = "Delete Failed")
            }
    )
    @DeleteMapping(value = "/user/{username}")
    public ResultVO<Void> deleteUser(@Parameter(name = "username", description = "Username of the user to be deleted")
                                         @PathVariable("username") String username,
                                     HttpServletRequest request) {
        Boolean result = userService.deleteUser(username, ((UserProfileDTO)
                request.getSession().getAttribute("account")).getUsername());
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
    @Operation(
            summary = "Delete users",
            description = "Batch delete users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Delete Success"),
                    @ApiResponse(responseCode = "444", description = "Delete Failed")
            }
    )
    @DeleteMapping(value = "/users")
    public ResultVO<Void> deleteUsers(@Parameter(name = "usernames", description = "Username list of the users to be deleted")
                                          @RequestBody List<String> usernames,
                                      HttpServletRequest request) {
        Boolean result = userService.deleteUsers(usernames, ((UserProfileDTO)
                request.getSession().getAttribute("account")).getUsername());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "delete success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "delete failed");
    }

    /**
     * Count all CA
     *
     * @param condition the condition of the CA
     * @return count result
     */
    @Operation(
            summary = "Count all CA",
            description = "Calculate the total number of CA",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count Success")
            }
    )
    @GetMapping(value = "/cert/ca/count")
    public ResultVO<Long> countAllCa(@Parameter(name = "condition", description = "Condition of the CA")
                                         @RequestParam(value = "condition", defaultValue = "none", required = false) String condition) {
        if ( "available".equals(condition) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Success",
                    caService.countAllCa(1));
        } else if ( "unavailable".equals(condition) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Success",
                    caService.countAllCa(0));
        } else {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Success",
                    caService.countAllCa(-1));
        }
    }

    /**
     * Count all ssl certificates
     *
     * @return count result
     */
    @Operation(
            summary = "Count all ssl certificates",
            description = "Calculate the total number of ssl certificates",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count Success")
            }
    )
    @GetMapping(value = "/cert/ssl/count")
    public ResultVO<Long> countAllCertificate() {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                certificateService.countAllCertificates());
    }

    /**
     * Count all users
     *
     * @param role the role of the user
     * @return count result
     */
    @Operation(
            summary = "Count all users",
            description = "Calculate the total number of users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count Success")
            }
    )
    @GetMapping(value = "/user/count")
    public ResultVO<Long> countAllUser(@Parameter(name = "role", description = "Role of the user")
                                           @RequestParam(value = "role", defaultValue = "0", required = false) Integer role) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                userService.countAllUser(role));
    }

}
