package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gregperlinli.certvault.certificate.CaGenerator;
import com.gregperlinli.certvault.constant.AccountTypeConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.entities.*;
import com.gregperlinli.certvault.domain.exception.LoginException;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.CaMapper;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import com.gregperlinli.certvault.utils.EncryptAndDecryptUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
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

    @Resource
    ICaBindingService caBindingService;

    @Override
    public PageDTO<CaInfoDTO> getCas(String keyword, String owner, Integer page, Integer limit) {
        Page<Ca> caPage = new Page<>(page, limit);
        Page<Ca> resultPage;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        if ( keyword == null || keyword.isEmpty() ) {
            if ( Objects.equals( AccountTypeConstant.SUPERADMIN.getAccountType(), user.getRole() ) ) {
                caQueryWrapper.eq("deleted", false);
            } else {
                caQueryWrapper.eq("owner", user.getId())
                        .eq("deleted", false);
            }
        } else {
            if ( Objects.equals( AccountTypeConstant.SUPERADMIN.getAccountType(), user.getRole() ) ) {
                caQueryWrapper.and(wrapper -> wrapper
                                .like("uuid", keyword)
                                .or()
                                .like("comment", keyword))
                        .eq("deleted", false);
            } else {
                caQueryWrapper.and(wrapper -> wrapper
                                .like("uuid", keyword)
                                .or()
                                .like("comment", keyword))
                        .eq("owner", user.getId())
                        .eq("deleted", false);
            }
        }
        resultPage = this.page(caPage, caQueryWrapper);
        if ( resultPage.getSize() == 0 || resultPage.getRecords() == null || resultPage.getRecords().isEmpty() ) {
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
                    dto.setParentCa(ca.getParentCa());
                    dto.setAllowSubCa(ca.getAllowSubCa());
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
    public PageDTO<CaInfoDTO> getBoundCas(String keyword, String username, Integer page, Integer limit) {
        Page<Ca> caPage = new Page<>(page, limit);
        Page<Ca> resultPage;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<CaBinding> caBindingQueryWrapper = new QueryWrapper<>();
        caBindingQueryWrapper.eq("uid", user.getId());
        List<CaBinding> caBindings = caBindingService.list(caBindingQueryWrapper);
        if ( caBindings.isEmpty() ) {
            return new PageDTO<>(0L, null);
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        if ( keyword == null || keyword.isEmpty() ) {
            caQueryWrapper.in("uuid", caBindings.stream().map(CaBinding::getCaUuid).toList())
                    .eq("available", true)
                    .eq("deleted", false);
        } else {
            caQueryWrapper.like("uuid", keyword)
                    .or()
                    .like("comment", keyword)
                    .in("uuid", caBindings.stream().map(CaBinding::getCaUuid).toList())
                    .eq("available", true)
                    .eq("deleted", false);
        }
        resultPage = this.page(caPage, caQueryWrapper);
        if ( resultPage.getSize() == 0 || resultPage.getRecords() == null || resultPage.getRecords().isEmpty() ) {
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
                    dto.setParentCa(ca.getParentCa());
                    dto.setAllowSubCa(ca.getAllowSubCa());
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
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        QueryWrapper<CaBinding> caBindingQueryWrapper = new QueryWrapper<>();
        caBindingQueryWrapper.eq("uid", user.getId())
                            .eq("ca_uuid", uuid);
        if (
                ( caBindingService.getOne(caBindingQueryWrapper) != null && ca.getAvailable() ) ||
                Objects.equals( ca.getOwner(), user.getId() ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            return ca.getCert();
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }

    @Override
    public String getCaCertChain(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                .eq("deleted", false);
        Ca currentCa = this.getOne(caQueryWrapper);
        if ( currentCa == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The certificate does not exist.");
        }
        if ( !Objects.equals( currentCa.getOwner(), user.getId() ) &&
                !( user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                        caBindingService.exists( new QueryWrapper<CaBinding>()
                                .eq("ca_uuid", currentCa.getParentCa())
                                .eq("uid", user.getId())) ) &&
                user.getRole() != AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            throw new ParamValidateException(
                    ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The certificate is not yours."
            );
        }
        // 收集证书链（处理二次编码）
        List<String> certChain = new ArrayList<>();
        // 处理当前证书
        String certBase64 = currentCa.getCert();
        if (certBase64 != null && !certBase64.isEmpty()) {
            try {
                // 解码二次编码的PEM字符串（原始PEM内容）
                String pemContent = new String(
                        Base64.getDecoder().decode(certBase64),
                        StandardCharsets.UTF_8
                );
                certChain.add(pemContent);
            } catch (IllegalArgumentException e) {
                throw new ParamValidateException(
                        ResultStatusCodeConstant.FAILED.getResultCode(),
                        "Invalid certificate format in database."
                );
            }
        }
        // 向上查找CA链
        String currentCaUuid = currentCa.getParentCa();
        while (currentCaUuid != null && !currentCaUuid.isEmpty()) {
            Ca ca = this.getOne(new QueryWrapper<Ca>()
                    .eq("uuid", currentCaUuid)
                    .eq("deleted", false));
            if (ca == null) {
                break;
            }
            String caCertBase64 = ca.getCert();
            if (caCertBase64 != null && !caCertBase64.isEmpty()) {
                try {
                    String pemContent = new String(
                            Base64.getDecoder().decode(caCertBase64),
                            StandardCharsets.UTF_8
                    );
                    certChain.add(pemContent);
                } catch (IllegalArgumentException e) {
                    throw new ParamValidateException(
                            ResultStatusCodeConstant.FAILED.getResultCode(),
                            "Invalid CA certificate format in database."
                    );
                }
            }
            currentCaUuid = ca.getParentCa();
        }
        // 拼接证书链并Base64编码
        String chainContent = String.join("\n", certChain);
        return Base64.getEncoder().encodeToString(
                chainContent.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String getCaPrivKey(String uuid, String confirmPassword, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        if ( !AuthUtils.matchesPassword(confirmPassword, user.getPassword()) ) {
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
        if (
                Objects.equals( ca.getOwner(), user.getId() ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            return EncryptAndDecryptUtils.decrypt(ca.getPrivkey());
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }

    @Override
    public Boolean updateCaComment(String uuid, String owner, String comment) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        if (
                Objects.equals( ca.getOwner(), user.getId() ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            ca.setComment(comment);
            ca.setModifiedAt(LocalDateTime.now());
            return this.updateById(ca);
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }

    @Override
    public Boolean modifyCaAvailability(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        if (
                Objects.equals( ca.getOwner(), user.getId() ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            UpdateWrapper<Ca> caUpdateWrapper = new UpdateWrapper<>();
            caUpdateWrapper.eq("uuid", uuid)
                        .set("available", !ca.getAvailable());
            boolean result = this.update(caUpdateWrapper);
            if ( result ) {
                return !ca.getAvailable();
            }
            throw new ParamValidateException(ResultStatusCodeConstant.FAILED.getResultCode(), "Update failed.");
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }

    @Override
    public ResponseCaDTO requestCa(RequestCertDTO requestCertDTO, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        LocalDateTime now = LocalDateTime.now();
        boolean result = false;
        GenResponse genResponse = null;
        if ( requestCertDTO.getCaUuid() == null || requestCertDTO.getCaUuid().isEmpty() ) {
            genResponse = CaGenerator.generateCaCertificate(new CaGenRequest(requestCertDTO, user.getEmail()));
            result = this.save(genResponse.toCa(user.getId(), now, now));
        } else {
            QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
            caQueryWrapper.eq("uuid", requestCertDTO.getCaUuid())
                    .eq("available", true)
                    .eq("deleted", false);
            Ca ca = this.getOne(caQueryWrapper);
            if ( !ca.getAllowSubCa() ) {
                throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA does not allow sub CA.");
            }
            QueryWrapper<CaBinding> caBindingQueryWrapper = new QueryWrapper<>();
            caBindingQueryWrapper.eq("uid", user.getId())
                    .eq("ca_uuid", requestCertDTO.getCaUuid());
            if (
                    ( caBindingService.getOne(caBindingQueryWrapper) != null && ca.getAvailable() ) ||
                    Objects.equals( ca.getOwner(), user.getId() ) ||
                    user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
            ) {
                genResponse = CaGenerator.generateCaCertificate(new CaGenRequest(requestCertDTO, EncryptAndDecryptUtils.decrypt(ca.getPrivkey()), ca.getCert(), requestCertDTO.getAllowSubCa(), user.getEmail()));
                result = this.save(genResponse.toIntCa(ca.getUuid(), requestCertDTO.getAllowSubCa(), user.getId(), now, now));
            } else {
                throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
            }
        }
        if ( result ) {
            CaBinding caBinding = new CaBinding();
            caBinding.setCaUuid(genResponse.getUuid());
            caBinding.setUid(user.getId());
            caBinding.setCreatedAt(now);
            caBindingService.save(caBinding);
            return new ResponseCaDTO(genResponse, requestCertDTO.getCaUuid(), requestCertDTO.getAllowSubCa());
        }
        return null;
    }

    @Override
    public ResponseCaDTO renewCa(String oldCaUuid, Integer expiry, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", oldCaUuid)
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        if (
                !Objects.equals( ca.getOwner(), user.getId() ) &&
                user.getRole() != AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
        }
        CaRenewRequest caRenewRequest = new CaRenewRequest(ca, expiry);
        if ( ca.getParentCa() != null ) {
            caQueryWrapper.clear();
            caQueryWrapper.eq("uuid", ca.getParentCa());
            Ca parentCa = this.getOne(caQueryWrapper);
            if ( parentCa == null ) {
                throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The parent CA does not exist.");
            }
            caRenewRequest.setParentCa(parentCa.getCert());
            caRenewRequest.setParentCaPrivkey(EncryptAndDecryptUtils.decrypt(parentCa.getPrivkey()));
        }
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
            return new ResponseCaDTO(genResponse, ca.getParentCa(), ca.getAllowSubCa());
        }
        return null;
    }

    @Override
    public Boolean deleteCa(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", uuid)
                    .eq("deleted", false);
        Ca ca = this.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        if (
                Objects.equals( ca.getOwner(), user.getId() ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            ca.setModifiedAt(LocalDateTime.now());
            ca.setDeleted(true);
            return this.update(ca, caQueryWrapper);
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }
}
