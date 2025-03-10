package com.gregperlinli.certvault.service.impl;

import com.gregperlinli.certvault.domain.entities.CaBinding;
import com.gregperlinli.certvault.mapper.CaBindingMapper;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 分配 CA 用户 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class CaBindingServiceImpl extends ServiceImpl<CaBindingMapper, CaBinding> implements ICaBindingService {

}
