package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gregperlinli.certvault.certificate.SslCertGenerator;
import com.gregperlinli.certvault.constant.AccountTypeConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.entities.*;
import com.gregperlinli.certvault.domain.exception.LoginException;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.CertificateMapper;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.gregperlinli.certvault.service.interfaces.ICertificateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import com.gregperlinli.certvault.utils.EncryptAndDecryptUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SSL Certificate Service Implementation Class
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements ICertificateService {

    @Resource
    IUserService userService;

    @Resource
    ICaService caService;

    @Resource
    ICaBindingService caBindingService;

    @Override
    public PageDTO<CertInfoDTO> getCertificates(String owner, Integer page, Integer limit) {
        Page<Certificate> certificatePage = new Page<>(page, limit);
        Page<Certificate> resultPage;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("owner", user.getId())
                            .eq("deleted", false);
        resultPage = this.page(certificatePage, certificateQueryWrapper);
        if ( resultPage.getSize() == 0 || resultPage.getRecords() == null || resultPage.getRecords().isEmpty() ) {
            return new PageDTO<>(resultPage.getTotal(), null);
        }
        Set<Integer> ownerIds = resultPage.getRecords().stream()
                .map(Certificate::getOwner).collect(Collectors.toSet());
        Map<Integer, String> userMap = userService.listByIds(ownerIds)
                .stream().collect(Collectors.toMap(User::getId, User::getUsername));
        return new PageDTO<>(resultPage.getTotal(),
                resultPage.getRecords().stream().map(certificate -> {
                    CertInfoDTO dto = new CertInfoDTO();
                    dto.setUuid(certificate.getUuid());
                    dto.setCaUuid(certificate.getCaUuid());
                    dto.setComment(certificate.getComment());
                    dto.setNotBefore(certificate.getNotBefore());
                    dto.setNotAfter(certificate.getNotAfter());
                    dto.setCreatedAt(certificate.getCreatedAt());
                    dto.setModifiedAt(certificate.getModifiedAt());
                    dto.setOwner(userMap.getOrDefault(certificate.getOwner(), "Unknown"));
                    return dto;
                }).toList());
    }

    @Override
    public String getCertificateCert(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid", uuid)
                            .eq("deleted", false);
        Certificate certificate = this.getOne(certificateQueryWrapper);
        if ( certificate == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The certificate does not exist.");
        }
        if (
                Objects.equals(certificate.getOwner(), user.getId()) ||
                (
                        user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                        caBindingService.exists(new QueryWrapper<CaBinding>()
                                .eq("ca_uuid", certificate.getCaUuid())
                                .eq("uid", user.getId()))
                ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            return certificate.getCert();
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The certificate is not yours.");
    }

    @Override
    public String getCertificatePrivkey(RequestPrivkeyDTO requestPrivkeyDTO, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        if ( !AuthUtils.matchesPassword(requestPrivkeyDTO.getPassword(), user.getPassword()) ) {
            throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "Password validation failed.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid", requestPrivkeyDTO.getUuid())
                            .eq("deleted", false);
        Certificate certificate = this.getOne(certificateQueryWrapper);
        if ( certificate == null) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The certificate does not exist.");
        }
        if (
                Objects.equals(certificate.getOwner(), user.getId()) ||
                (
                        user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                        caBindingService.exists(new QueryWrapper<CaBinding>()
                                .eq("ca_uuid", certificate.getCaUuid())
                                .eq("uid", user.getId()))
                ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            return EncryptAndDecryptUtils.decrypt(certificate.getPrivkey());
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The certificate is not yours.");
    }

    @Override
    public Boolean updateCertComment(UpdateCommentDTO updateCommentDTO, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid", updateCommentDTO.getUuid())
                            .eq("deleted", false);
        Certificate certificate = this.getOne(certificateQueryWrapper);
        if ( certificate == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The certificate does not exist.");
        }
        if (
                Objects.equals(certificate.getOwner(), user.getId()) ||
                (
                        user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                        caBindingService.exists(new QueryWrapper<CaBinding>()
                                .eq("ca_uuid", certificate.getCaUuid())
                                .eq("uid", user.getId()))
                ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            certificate.setComment(updateCommentDTO.getComment());
            certificate.setModifiedAt(LocalDateTime.now());
            return this.updateById(certificate);
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The certificate is not yours.");
    }

    @Override
    public ResponseCertDTO requestCert(RequestCertDTO requestCertDTO, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
        caQueryWrapper.eq("uuid", requestCertDTO.getCaUuid())
                    .eq("available", true)
                    .eq("deleted", false);
        Ca ca = caService.getOne(caQueryWrapper);
        QueryWrapper<CaBinding> caBindingQueryWrapper = new QueryWrapper<>();
        caBindingQueryWrapper.eq("uid", user.getId())
                .eq("ca_uuid", requestCertDTO.getCaUuid());
        if (
                ( caBindingService.getOne(caBindingQueryWrapper) != null && ca.getAvailable() ) ||
                Objects.equals( ca.getOwner(), user.getId() ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            GenResponse genResponse = SslCertGenerator.generateSslCertificate(new CertGenRequest(requestCertDTO, EncryptAndDecryptUtils.decrypt(ca.getPrivkey()), ca.getCert(), user.getEmail()));
            LocalDateTime now = LocalDateTime.now();
            Certificate certificate = genResponse.toCert(ca.getUuid(), user.getId(), now, now);
            if ( this.save(certificate) ) {
                return new ResponseCertDTO(genResponse, ca.getUuid());
            }
            return null;
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }

    @Override
    public ResponseCertDTO renewCert(String oldCertUuid, Integer expiry, String owner) throws Exception {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid", oldCertUuid)
                            .eq("deleted", false);
        Certificate certificate = this.getOne(certificateQueryWrapper);
        if ( certificate == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The certificate does not exist.");
        }
        if (
                Objects.equals(certificate.getOwner(), user.getId()) ||
                (
                        user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                        caBindingService.exists(new QueryWrapper<CaBinding>()
                                .eq("ca_uuid", certificate.getCaUuid())
                                .eq("uid", user.getId()))
                ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            QueryWrapper<Ca> caQueryWrapper = new QueryWrapper<>();
            caQueryWrapper.eq("uuid", certificate.getCaUuid())
                        .eq("available", true)
                        .eq("deleted", false);
            Ca ca = caService.getOne(caQueryWrapper);
            if ( ca == null ) {
                throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
            }
            GenResponse genResponse = SslCertGenerator.renewSslCertificate(new CertRenewRequest(EncryptAndDecryptUtils.decrypt(ca.getPrivkey()), ca.getCert(), oldCertUuid, EncryptAndDecryptUtils.decrypt(ca.getPrivkey()), certificate.getCert(), expiry, certificate.getComment()));
            certificate.setCert(genResponse.getCert());
            certificate.setNotBefore(genResponse.getNotBefore());
            certificate.setNotAfter(genResponse.getNotAfter());
            certificate.setModifiedAt(LocalDateTime.now());
            UpdateWrapper<Certificate> certificateUpdateWrapper = new UpdateWrapper<>();
            certificateUpdateWrapper.eq("uuid", oldCertUuid);
            boolean result = this.update(certificate, certificateUpdateWrapper);
            if ( result ) {
                genResponse.setPrivkey(null);
                return new ResponseCertDTO(genResponse, ca.getUuid());
            }
            return null;
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The certificate is not yours.");
    }

    @Override
    public Boolean deleteCert(String uuid, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid", uuid)
                            .eq("deleted", false);
        Certificate certificate = this.getOne(certificateQueryWrapper);
        if ( certificate == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The certificate does not exist.");
        }
        if (
                Objects.equals( certificate.getOwner(), user.getId() ) ||
                (
                        user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                        caBindingService.exists(new QueryWrapper<CaBinding>()
                                .eq("ca_uuid", certificate.getCaUuid())
                                .eq("uid", user.getId()))
                ) ||
                user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            certificate.setModifiedAt(LocalDateTime.now());
            certificate.setDeleted(true);
            return this.updateById(certificate);
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The certificate is not yours.");
    }
}
