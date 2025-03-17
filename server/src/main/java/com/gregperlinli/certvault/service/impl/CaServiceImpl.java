package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gregperlinli.certvault.certificate.CaGenerator;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.CaInfoDTO;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.dto.RequestCertDTO;
import com.gregperlinli.certvault.domain.dto.ResponseCaDTO;
import com.gregperlinli.certvault.domain.entities.*;
import com.gregperlinli.certvault.domain.exception.LoginException;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.CaMapper;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import com.gregperlinli.certvault.utils.EncryptAndDecryptUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Resource
    IUserService userService;

    @Override
    public PageDTO<CaInfoDTO> getCas(String owner, Integer page, Integer limit) {
        Page<Ca> caPage = new Page<>(page, limit);
        Page<Ca> resultPage;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("owner", user.getId())
                .eq("deleted", false);
        resultPage = this.page(caPage, caQueryWrapper);
        if ( resultPage.getSize() == 0 || resultPage.getRecords() == null ) {
            return new PageDTO<>(resultPage.getTotal(), null);
        }
        Set<Integer> ownerIds = resultPage.getRecords().stream()
                .map(Ca::getOwner).collect(Collectors.toSet());
        Map<Integer, String> userMap = userService.listByIds(ownerIds)
                .stream().collect(Collectors.toMap(User::getId, User::getUsername));
        return new PageDTO<>(resultPage.getTotal(),
                resultPage.getRecords().stream().map(ca -> {
                    CaInfoDTO dto = new CaInfoDTO();
                    dto.setUuid(ca.getUuid());
                    dto.setComment(ca.getComment());
                    dto.setAvailable(ca.getAvailable());
                    dto.setNotBefore(ca.getNotBefore());
                    dto.setNotAfter(ca.getNotAfter());
                    dto.setCreatedAt(ca.getCreatedAt());
                    dto.setModifiedAt(ca.getModifiedAt());
                    dto.setOwner(userMap.getOrDefault(ca.getOwner(), "Unknown"));
                    return dto;
                }).toList());
    }

    @Override
    public String getCaCert(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                    .eq("owner", user.getId())
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        return ca.getCert();
    }

    @Override
    public String getCaPrivKey(String uuid, String owner, String password) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null || !AuthUtils.matchesPassword(password, user.getPassword()) ) {
            throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "Password validation failed.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                    .eq("owner", user.getId())
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        return EncryptAndDecryptUtils.decrypt(ca.getPrivkey());
    }

    @Override
    public Boolean updateCaComment(String uuid, String owner, String comment) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        UpdateWrapper<Ca> caUpdateWrapper = new UpdateWrapper<>();
        caUpdateWrapper.eq("uuid", uuid)
                    .eq("owner", user.getId())
                    .eq("deleted", false)
                    .set("comment", comment)
                    .set("modified_at", LocalDateTime.now());
        return this.update(caUpdateWrapper);
    }

    @Override
    public ResponseCaDTO requestCa(RequestCertDTO requestCertDTO, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        GenResponse genResponse = CaGenerator.generateCaCertificate(new CaGenRequest(requestCertDTO, user.getEmail()));
        LocalDateTime now = LocalDateTime.now();
        boolean result = this.save(GenResponse.toCa(genResponse, user.getId(), now, now));
        if ( result ) {
            return new ResponseCaDTO(genResponse);
        }
        return null;
    }

    @Override
    public ResponseCaDTO renewCa(String oldCaUuid, Integer expiry, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", oldCaUuid)
                    .eq("owner", user.getId())
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        CaRenewRequest caRenewRequest = new CaRenewRequest(ca, expiry);
        GenResponse genResponse = CaGenerator.renewCaCertificate(caRenewRequest);
        ca.setCert(genResponse.getCert());
        ca.setNotBefore(genResponse.getNotBefore());
        ca.setNotAfter(genResponse.getNotAfter());
        ca.setModifiedAt(LocalDateTime.now());
        UpdateWrapper<Ca> caUpdateWrapper = new UpdateWrapper<>();
        caUpdateWrapper.eq("uuid", oldCaUuid);
        boolean result = this.update(ca, caUpdateWrapper);
        if ( result ) {
            genResponse.setPrivkey(null);
            return new ResponseCaDTO(genResponse);
        }
        return null;
    }

    @Override
    public Boolean deleteCa(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner);
        User user = userService.getOne(userQueryWrapper);
        UpdateWrapper<Ca> caUpdateWrapper = new UpdateWrapper<>();
        caUpdateWrapper.eq("uuid", uuid)
                    .eq("owner", user.getId())
                    .eq("deleted", false)
                    .set("deleted", true)
                    .set("modified_at", LocalDateTime.now());
        return this.update(caUpdateWrapper);
    }
}
