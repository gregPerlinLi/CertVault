package com.gregperlinli.certvault.service.impl;

import com.gregperlinli.certvault.domain.entities.Role;
import com.gregperlinli.certvault.mapper.RoleMapper;
import com.gregperlinli.certvault.service.interfaces.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
