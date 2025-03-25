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
import com.gregperlinli.certvault.mapper.CaMapper;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
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

    @Resource
    CaMapper caMapper;

    @Override
    public PageDTO<CertInfoDTO> getCertificates(String keyword, String owner, Integer page, Integer limit) {
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
        if ( keyword == null || keyword.isEmpty() ) {
            if ( Objects.equals(AccountTypeConstant.ADMIN.getAccountType(), user.getRole()) ) {
                List<Object> uuids = caMapper.selectObjs(
                        new QueryWrapper<Ca>()
                                .select("uuid")
                                .eq("owner", user.getId())
                                .eq("deleted", false)
                );
                List<String> caUuids = uuids.stream().map(Object::toString).toList();
                certificateQueryWrapper.or()
                        .in("ca_uuid", caUuids)
                        .eq("deleted", false);
            } else if ( Objects.equals(AccountTypeConstant.SUPERADMIN.getAccountType(), user.getRole()) ) {
                certificateQueryWrapper.eq("deleted", false);
            } else {
                certificateQueryWrapper.eq("owner", user.getId())
                        .eq("deleted", false);
            }
        } else {
            if ( Objects.equals( AccountTypeConstant.ADMIN.getAccountType(), user.getRole() ) ) {
                List<Object> uuids = caMapper.selectObjs(
                        new QueryWrapper<Ca>()
                                .select("uuid")
                                .eq("owner", user.getId())
                                .eq("deleted", false)
                );
                List<String> caUuids = uuids.stream().map(Object::toString).toList();
                certificateQueryWrapper.and(wrapper -> wrapper
                                .like("uuid", keyword)
                                .or()
                                .like("comment", keyword)
                                )
                        .in("ca_uuid", caUuids)
                        .eq("deleted", false);
            } else if ( Objects.equals( AccountTypeConstant.SUPERADMIN.getAccountType(), user.getRole() ) ) {
                certificateQueryWrapper.and(wrapper -> wrapper
                                .like("uuid", keyword)
                                .or()
                                .like("comment", keyword))
                        .eq("deleted", false);
            } else {
                certificateQueryWrapper.and(wrapper -> wrapper
                                .like("uuid", keyword)
                                .or()
                                .like("comment", keyword))
                        .eq("owner", user.getId())
                        .eq("deleted", false);
            }
        }
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
    public String getCertificateCertChain(String uuid, String owner, Boolean needRootCa) {
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
        if ( !Objects.equals( certificate.getOwner(), user.getId() ) &&
             !( user.getRole() == AccountTypeConstant.ADMIN.getAccountType() &&
                     caBindingService.exists( new QueryWrapper<CaBinding>()
                             .eq("ca_uuid", certificate.getCaUuid())
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
        String certBase64 = certificate.getCert();
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
        String currentCaUuid = certificate.getCaUuid();
        while (currentCaUuid != null && !currentCaUuid.isEmpty()) {
            Ca ca = caService.getOne(new QueryWrapper<Ca>()
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
        // 根据needRootCa参数决定是否移除根CA
        if (!needRootCa && !certChain.isEmpty()) {
            // 根CA是最后一个添加的证书，移除它
            certChain.remove(certChain.size() - 1);
        }
        // 拼接证书链并Base64编码
        String chainContent = String.join("\n", certChain);
        return Base64.getEncoder().encodeToString(
                chainContent.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String getCertificatePrivkey(String uuid, String confirmPassword, String owner) throws Exception {
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
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid", uuid)
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
    public Boolean updateCertComment(String uuid, String comment, String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                        .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<Certificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("uuid",uuid)
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
            certificate.setComment(comment);
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

    @Override
    public Long countCertificates(String owner) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", owner)
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        return this.count(new QueryWrapper<Certificate>().eq("owner", user.getId()).eq("deleted", false));
    }

    @Override
    public Long countAllCertificates() {
        return this.count(new QueryWrapper<Certificate>().eq("deleted", false));
    }

    @Override
    public Long countCaSigned(String owner, String uuid, Boolean caOrSsl) {
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
        Ca ca = caService.getOne(caQueryWrapper);
        if ( ca == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The CA does not exist.");
        }
        if (
                Objects.equals( ca.getOwner(), user.getId() ) ||
                        user.getRole() == AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            if ( caOrSsl ) {
                return caService.count(new QueryWrapper<Ca>().eq("parent_ca", uuid).eq("deleted", false));
            } else {
                return this.count(new QueryWrapper<Certificate>().eq("ca_uuid", uuid).eq("deleted", false));
            }
        }
        throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The CA is not yours.");
    }
}
