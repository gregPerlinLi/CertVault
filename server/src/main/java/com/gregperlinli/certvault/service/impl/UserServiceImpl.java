package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gregperlinli.certvault.constant.RedisKeyConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.UserMapper;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className UserServiceImpl
 * @since 2025-03-03
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final int BATCH_SIZE = 500;

    //////////////////////////////////////////////////
    //   The following is the general user method   //
    //////////////////////////////////////////////////

    @Override
    public UserProfileDTO login(String username, String password, String sessionId) {
        if ( !GenericUtils.allOfNullable(username, password) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be null.");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                        .eq("deleted", false);
        User user = this.getOne(userQueryWrapper);
        if ( user != null && AuthUtils.matchesPassword(password, user.getPassword()) ) {
            stringRedisTemplate.opsForValue().set(RedisKeyConstant.USER.joinLoginPrefix(sessionId), user.getUsername(), 60, TimeUnit.MINUTES);
            return new UserProfileDTO(user);
        }
        return null;
    }

    @Override
    public boolean loginVerify(String username, String sessionId) {
        if ( !GenericUtils.ofNullable(sessionId) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be empty.");
        }
        String loginUser = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER.joinLoginPrefix(sessionId));
        return username.equals(loginUser);
    }

    @Override
    public void logout(String sessionId) {
        if ( !GenericUtils.ofNullable(sessionId) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be empty.");
        }
        stringRedisTemplate.delete(RedisKeyConstant.USER.joinLoginPrefix(sessionId));
    }

    @Override
    public UserProfileDTO getOwnProfile(String username) {
        if ( !GenericUtils.ofNullable(username) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be empty.");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        if ( user != null ) {
            return new UserProfileDTO(user);
        }
        throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user does not exist.");
    }

    @Override
    public Boolean updateUserProfile(String username, UpdateUserProfileDTO updateUserProfileDTO, boolean isSuperadmin) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user does not exist.");
        }
        if ( GenericUtils.ofNullable(updateUserProfileDTO.getOldPassword()) || GenericUtils.ofNullable(updateUserProfileDTO.getNewPassword()) ) {
            if ( isSuperadmin || AuthUtils.matchesPassword(updateUserProfileDTO.getOldPassword(), user.getPassword()) ) {
                user.setPassword(AuthUtils.encryptPassword(updateUserProfileDTO.getNewPassword()));
            } else {
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The old password is incorrect.");
            }
        }
        if ( GenericUtils.ofNullable(updateUserProfileDTO.getDisplayName()) ) {
            user.setDisplayName(updateUserProfileDTO.getDisplayName());
        }
        if ( GenericUtils.ofNullable(updateUserProfileDTO.getEmail()) ) {
            user.setEmail(updateUserProfileDTO.getEmail());
        }
        user.setModifiedAt(LocalDateTime.now());
        return this.updateById(user);
    }

    //////////////////////////////////////////////////
    // The following is the admin/superadmin method //
    //////////////////////////////////////////////////


    @Override
    public PageDTO<UserProfileDTO> getAllUsers(Integer page, Integer limit) {
        Page<User> userPage = new Page<>(page, limit);
        Page<User> resultPage = this.page(userPage);
        return new PageDTO<>(resultPage.getTotal(),
                resultPage.getRecords().stream().map(UserProfileDTO::new).toList());
    }

    @Override
    public UserProfileDTO createUser(CreateUserDTO createUserDTO) throws Exception {
        if ( !GenericUtils.allOfNullable(createUserDTO) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter of the entity cannot be null.");
        }
        User user = createUserDTO.toUser();
        if ( this.save(user) ) {
            return new UserProfileDTO(user);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", user.getUsername())
                        .eq("deleted", true);
        if ( this.getOne(userQueryWrapper) != null ) {
            user = createUserDTO.updateUser(user);
            if ( this.updateById(user) ) {
                return new UserProfileDTO(user);
            }
        }
        throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user already exists.");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean createUsers(List<CreateUserDTO> createUserDTOs) throws Exception {
        // 1. Parameter verification
        for (CreateUserDTO dto : createUserDTOs) {
            if ( !GenericUtils.allOfNullable(dto) ) {
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter of the entity cannot be null.");
            }
        }

        // 2. Pre-check username existence
        List<String> usernames = createUserDTOs.stream()
                .map(CreateUserDTO::getUsername)
                .toList();

        // 3. Check for users that exist normally
        QueryWrapper<User> existsCheck = new QueryWrapper<>();
        existsCheck.in("username", usernames)
                .eq("deleted", false);
        List<User> existingUsers = list(existsCheck);
        if (!existingUsers.isEmpty()) {
            log.error("The following users already exist: {}", existingUsers.stream().map(User::getUsername).toList());
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                    "The following users already exist: " + existingUsers.stream().map(User::getUsername).toList());
        }

        // 4. Check deleted users (for update)
        QueryWrapper<User> deletedCheck = new QueryWrapper<>();
        deletedCheck.in("username", usernames)
                .eq("deleted", true);
        List<User> deletedUsers = list(deletedCheck);
        Map<String, User> deletedUserMap = deletedUsers.stream()
                .collect(Collectors.toMap(User::getUsername, user -> user));

        // 5. Process user data
        List<User> usersToInsert = new ArrayList<>();
        List<User> usersToUpdate = new ArrayList<>();

        for (CreateUserDTO dto : createUserDTOs) {
            User existingDeletedUser = deletedUserMap.get(dto.getUsername());
            if (existingDeletedUser != null) {
                // Cover deleted users
                User updateUser = dto.updateUser(existingDeletedUser);
                updateUser.setModifiedAt(LocalDateTime.now());
                usersToUpdate.add(updateUser);
            } else {
                // New users
                User newUser = dto.toUser();
                newUser.setCreatedAt(LocalDateTime.now());
                usersToInsert.add(newUser);
            }
        }

        // 6. Batch insert new users
        BatchProcessingUtils.batchProcess(usersToInsert, BATCH_SIZE, this::saveBatch);

        // 7. Batch update deleted users
        BatchProcessingUtils.batchProcess(usersToUpdate, BATCH_SIZE, this::updateBatchById);
        return true;
    }

    @Override
    public Boolean updateUserRole(UpdateRoleDTO updateRoleDTO, String requestUser) throws Exception {
        if ( !GenericUtils.allOfNullable(updateRoleDTO) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter of the entity cannot be empty.");
        }
        if (Objects.equals( updateRoleDTO.getUsername(), requestUser ) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "You cannot modify your own role.");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", updateRoleDTO.getUsername())
                        .eq("deleted", false);
        User user = this.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user does not exist.");
        }
        user.setRole(updateRoleDTO.getRole());
        user.setModifiedAt(LocalDateTime.now());
        return this.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateUsersRole(List<UpdateRoleDTO> updateRoleDTOs, String requestUser) throws Exception {
        // 1. Parameter verification
        for ( UpdateRoleDTO updateRoleDTO : updateRoleDTOs ) {
            if ( !GenericUtils.allOfNullable(updateRoleDTO) ) {
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter of the entity cannot be empty.");
            }
            if ( Objects.equals( updateRoleDTO.getUsername(), requestUser ) ) {
                throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "You cannot modify your own role.");
            }
        }

        // 2. Check for users that exist normally
        QueryWrapper<User> existCheck = new QueryWrapper<>();
        existCheck.in("username", updateRoleDTOs.stream().map(UpdateRoleDTO::getUsername).toList())
                .eq("deleted", false);
        List<User> existingUsers = list(existCheck);
        Set<String> existingUsernames = existingUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        // 3. Check if all usernames exist
        for ( UpdateRoleDTO updateRoleDTO : updateRoleDTOs ) {
            if ( !existingUsernames.contains(updateRoleDTO.getUsername()) ) {
                log.error("The following users do not exist: {}", updateRoleDTO.getUsername());
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                        "The following users do not exist: " + updateRoleDTO.getUsername());
            }
        }

        // 4. Process user data
        List<User> userBatchList = updateRoleDTOs.stream()
                .map(updateRoleDTO -> {
                    User user = new User();
                    user.setUsername(updateRoleDTO.getUsername());
                    user.setRole(updateRoleDTO.getRole());
                    user.setModifiedAt(LocalDateTime.now());
                    return user;
                })
                .toList();

        // 5. Batch update
        BatchProcessingUtils.batchProcess(userBatchList, BATCH_SIZE, (users) -> {
            List<String> batchUsernames = users.stream()
                    .map(User::getUsername)
                    .toList();

            UpdateWrapper<User> uw = new UpdateWrapper<>();
            uw.in("username", batchUsernames)
                    .set("role", userBatchList.get(0).getRole())
                    .set("modified_at", LocalDateTime.now());

            return update(uw);
        });
        return true;
    }

    @Override
    public Boolean deleteUser(String username, String requestUser) {
        if ( !GenericUtils.ofNullable(username) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be empty.");
        }
        if ( Objects.equals( username, requestUser ) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "You cannot delete your own account.");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                        .eq("deleted", false);
        User user = this.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user does not exist.");
        }
        user.setDeleted(true);
        user.setModifiedAt(LocalDateTime.now());
        return this.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteUsers(List<String> usernames, String requestUser) {
        // 1. Parameter verification
        for ( String username : usernames ) {
            if ( !GenericUtils.ofNullable(username) ) {
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user does not exist.");
            }
            if ( Objects.equals( username, requestUser ) ) {
                throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "You cannot delete your own account.");
            }
        }

        // 2. Check user existence
        QueryWrapper<User> existsCheck = new QueryWrapper<>();
        existsCheck.in("username", usernames)
                .eq("deleted", false);
        List<User> existingUsers = list(existsCheck);
        Set<String> existingUsernames = existingUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        // 3. Check if all usernames exist
        for (String username : usernames) {
            if (!existingUsernames.contains(username)) {
                log.error("User {} Does not exist.", username);
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                        "User " + username + " Does not exist.");
            }
        }

        // 4. Convert the username to a User object (only the username field is required).
        List<User> userBatchList = usernames.stream()
                .map(username -> {
                    User u = new User();
                    u.setUsername(username);
                    return u;
                })
                .collect(Collectors.toList());

        // 5. Batch delete users
        BatchProcessingUtils.batchProcess(userBatchList, BATCH_SIZE, (users) -> {
            List<String> batchUsernames = users.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            UpdateWrapper<User> uw = new UpdateWrapper<>();
            uw.in("username", batchUsernames)
                    .eq("deleted", false)
                    .set("deleted", true)
                    .set("modified_at", LocalDateTime.now());

            return update(uw);
        });

        return true;
    }


}
