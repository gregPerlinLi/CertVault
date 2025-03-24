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
import com.gregperlinli.certvault.utils.BatchProcessingUtils;
import com.gregperlinli.certvault.utils.GenericUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final int BATCH_SIZE = 500;

    @Override
    public Boolean newBinding(CaBindingDTO caBindingDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", caBindingDTO.getUsername())
                        .eq("deleted", false);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean newBindings(List<CaBindingDTO> caBindingDTOs) throws Exception {
        // 1. 参数校验
        for (CaBindingDTO dto : caBindingDTOs) {
            if ( !GenericUtils.allOfNullable(dto) ) {
                throw new ParamValidateException(
                        ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "Binding parameter cannot be null"
                );
            }
        }

        // 2. 检查用户存在性
        List<String> usernames = caBindingDTOs.stream()
                .map(CaBindingDTO::getUsername)
                .distinct()
                .collect(Collectors.toList());
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("username", usernames)
                .eq("deleted", false);
        List<User> existingUsers = userService.list(userQuery);
        Map<String, User> userMap = existingUsers.stream()
                .collect(Collectors.toMap(User::getUsername, user -> user));

        for (CaBindingDTO dto : caBindingDTOs) {
            if (!userMap.containsKey(dto.getUsername())) {
                throw new ParamValidateException(
                        ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                        "User [" + dto.getUsername() + "] does not exist"
                );
            }
        }

        // 3. 检查重复绑定
        List<CaBinding> existingBindings = new ArrayList<>();
        for (CaBindingDTO dto : caBindingDTOs) {
            User user = userMap.get(dto.getUsername());
            QueryWrapper<CaBinding> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("ca_uuid", dto.getCaUuid())
                    .eq("uid", user.getId());
            if (count(checkWrapper) > 0) {
                CaBinding caBinding = new CaBinding();
                caBinding.setCaUuid(dto.getCaUuid());
                caBinding.setUid(user.getId());
                existingBindings.add(caBinding);
            }
        }

        if (!existingBindings.isEmpty()) {
            throw new ParamValidateException(
                    ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                    "Duplicate bindings exist for: " + existingBindings.stream()
                            .map(b -> "CA["+b.getCaUuid()+"] User["+b.getUid()+"]")
                            .collect(Collectors.joining(", "))
            );
        }

        // 4. 构建批量插入数据
        List<CaBinding> bindings = caBindingDTOs.stream()
                .map(dto -> {
                    User user = userMap.get(dto.getUsername());
                    CaBinding binding = new CaBinding();
                    binding.setCaUuid(dto.getCaUuid());
                    binding.setUid(user.getId());
                    binding.setCreatedAt(LocalDateTime.now());
                    return binding;
                })
                .collect(Collectors.toList());

        // 5. 批量插入（分批次处理）
        BatchProcessingUtils.batchProcess(bindings, BATCH_SIZE, this::saveBatch);

        return true;

    }

    @Override
    public Boolean deleteBinding(CaBindingDTO caBindingDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", caBindingDTO.getUsername())
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<CaBinding> caBindingQueryWrapper = new QueryWrapper<>();
        caBindingQueryWrapper.eq("ca_uuid", caBindingDTO.getCaUuid())
                            .eq("uid", user.getId());
        return this.remove(caBindingQueryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteBindings(List<CaBindingDTO> caBindingDTOs) throws Exception {
        // 1. 参数校验
        for (CaBindingDTO dto : caBindingDTOs) {
            if ( !GenericUtils.allOfNullable(dto) ) {
                throw new ParamValidateException(
                        ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                        "Binding parameter cannot be null"
                );
            }
        }

        // 2. 检查用户存在性
        List<String> usernames = caBindingDTOs.stream()
                .map(CaBindingDTO::getUsername)
                .distinct()
                .collect(Collectors.toList());
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("username", usernames)
                .eq("deleted", false);
        List<User> existingUsers = userService.list(userQuery);
        Map<String, User> userMap = existingUsers.stream()
                .collect(Collectors.toMap(User::getUsername, user -> user));

        for (CaBindingDTO dto : caBindingDTOs) {
            if (!userMap.containsKey(dto.getUsername())) {
                throw new ParamValidateException(
                        ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                        "User [" + dto.getUsername() + "] does not exist"
                );
            }
        }

        // 3. 检查绑定存在性
        for (CaBindingDTO dto : caBindingDTOs) {
            User user = userMap.get(dto.getUsername());
            QueryWrapper<CaBinding> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("ca_uuid", dto.getCaUuid())
                    .eq("uid", user.getId());
            if (count(checkWrapper) == 0) {
                throw new ParamValidateException(
                        ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                        "Binding for CA [" + dto.getCaUuid() + "] and user [" + dto.getUsername() + "] does not exist"
                );
            }
        }

        // 4. 构建批量删除条件
        List<CaBinding> deleteConditions = caBindingDTOs.stream()
                .map(dto -> {
                    User user = userMap.get(dto.getUsername());
                    CaBinding condition = new CaBinding();
                    condition.setCaUuid(dto.getCaUuid());
                    condition.setUid(user.getId());
                    return condition;
                })
                .collect(Collectors.toList());

        // 5. 批量删除（分批次处理）
        BatchProcessingUtils.batchProcess(deleteConditions, BATCH_SIZE, (batch) -> {
            for (CaBinding binding : batch) {
                QueryWrapper<CaBinding> wrapper = new QueryWrapper<>();
                wrapper.eq("ca_uuid", binding.getCaUuid())
                        .eq("uid", binding.getUid());
                remove(wrapper);
            }
            return true;
        });

        return true;
    }

    @Override
    public Long countBoundCa(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        return this.count(new QueryWrapper<CaBinding>().eq("uid", user.getId()));
    }

}
