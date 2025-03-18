package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.CaBindingDTO;
import com.gregperlinli.certvault.domain.entities.CaBinding;
import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.CaBindingMapper;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Allocate CA Users Service Implementation Class
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class CaBindingServiceImpl extends ServiceImpl<CaBindingMapper, CaBinding> implements ICaBindingService {

    @Resource
    IUserService userService;

    @Override
    public Boolean newBinding(CaBindingDTO caBindingDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", caBindingDTO.getUsername());
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        CaBinding caBinding = new CaBinding();
        caBinding.setCaUuid(caBindingDTO.getCaUuid());
        caBinding.setUid(user.getId());
        caBinding.setCreatedAt(LocalDateTime.now());
        return this.save(caBinding);
    }

    @Override
    public Boolean deleteBinding(CaBindingDTO caBindingDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", caBindingDTO.getUsername());
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<CaBinding> caBindingQueryWrapper = new QueryWrapper<>();
        caBindingQueryWrapper.eq("ca_uuid", caBindingDTO.getCaUuid())
                            .eq("uid", user.getId());
        return this.remove(caBindingQueryWrapper);
    }
}
