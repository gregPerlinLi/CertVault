package com.gregperlinli.certvault.service.impl;

import com.gregperlinli.certvault.domain.entities.RoleBinding;
import com.gregperlinli.certvault.mappers.RoleBindingMapper;
import com.gregperlinli.certvault.service.interfaces.IRoleBindingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限绑定 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class RoleBindingServiceImpl extends ServiceImpl<RoleBindingMapper, RoleBinding> implements IRoleBindingService {

}
