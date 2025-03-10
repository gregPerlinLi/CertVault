package com.gregperlinli.certvault.service.impl;

import com.gregperlinli.certvault.domain.entities.Ca;
import com.gregperlinli.certvault.mapper.CaMapper;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * CA列表 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class CaServiceImpl extends ServiceImpl<CaMapper, Ca> implements ICaService {

}
