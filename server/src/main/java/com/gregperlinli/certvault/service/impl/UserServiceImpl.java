package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gregperlinli.certvault.constant.RedisKeyConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.dto.UpdateUserProfileDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.entities.RoleBinding;
import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.RoleBindingMapper;
import com.gregperlinli.certvault.mapper.UserMapper;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource()
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RoleBindingMapper roleBindingMapper;

    //////////////////////////////////////////////////
    //   The following is the general user method   //
    //////////////////////////////////////////////////

    @Override
    public UserProfileDTO login(String username, String password, String sessionId) {
        if ( !GenericUtils.allOfNullable(username, password) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be null.");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
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
        if ( user != null ) {
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
        throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The user does not exist.");
    }

    /*private List<Integer> getRoleBindings(User user) {
        QueryWrapper<RoleBinding> roleBindingQueryWrapper = new QueryWrapper<>();
        roleBindingQueryWrapper.eq("uid", user.getId());
        List<RoleBinding> roleBindings = roleBindingMapper.selectList(roleBindingQueryWrapper);
        return roleBindings.stream().map(RoleBinding::getRoleId).toList();
    }*/

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

}
