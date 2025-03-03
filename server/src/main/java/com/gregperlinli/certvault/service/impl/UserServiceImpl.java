package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gregperlinli.certvault.constant.RedisKeyConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.UserDTO;
import com.gregperlinli.certvault.domain.entities.Role;
import com.gregperlinli.certvault.domain.entities.RoleBinding;
import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mappers.RoleBindingMapper;
import com.gregperlinli.certvault.mappers.RoleMapper;
import com.gregperlinli.certvault.mappers.UserMapper;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

    @Resource
    StringRedisTemplate redisTemplate;

    @Resource
    RoleBindingMapper roleBindingMapper;

    @Resource
    RoleMapper roleMapper;

    @Override
    public UserDTO login(String username, String password, String sessionId) {
        if ( !GenericUtils.allOfNullable(username, password) ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The parameter cannot be null.");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        User user = this.getOne(userQueryWrapper);
        if ( user != null && AuthUtils.matchesPassword(password, user.getPassword()) ) {
            redisTemplate.opsForValue().set(RedisKeyConstant.USER.joinLoginPrefix(sessionId), user.getUsername(), 60, TimeUnit.MINUTES);
            QueryWrapper<RoleBinding> roleBindingQueryWrapper = new QueryWrapper<>();
            roleBindingQueryWrapper.eq("uid", user.getId());
            List<RoleBinding> roleBindings = roleBindingMapper.selectList(roleBindingQueryWrapper);
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.in("id", roleBindings.stream().map(RoleBinding::getRoleId).toList());
            List<Role> roles = roleMapper.selectList(roleQueryWrapper);
            return new UserDTO(user, roles);
        }
        return null;
    }
}
