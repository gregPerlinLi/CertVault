package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.entities.CertGenRequest;
import com.gregperlinli.certvault.domain.entities.CertRenewRequest;
import com.gregperlinli.certvault.domain.entities.GenResponse;
import com.gregperlinli.certvault.domain.entities.SubjectAltName;
import com.gregperlinli.certvault.domain.exception.CertGenException;
import com.gregperlinli.certvault.utils.CertUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.gregperlinli.certvault.domain.entities.SubjectAltName.Type.DNS_NAME;

/**
 * SSL Certificate Generator
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SslCertGenerator}
 * @date 2025/3/15 14:17
 */
public class SslCertGenerator {

    /**
     * 生成SSL证书
     * @param request 包含SSL证书信息的请求
     * @return 生成的SSL证书和私钥
     */
    public static GenResponse generateSslCertificate(CertGenRequest request) {
        try {
            // 1. 注册 Bouncy Castle 提供者
            Security.addProvider(new BouncyCastleProvider());

            // 2. 解析CA证书和私钥
            X509CertificateHolder caCert = CertUtils.parseCertificate(request.getCa());
            PrivateKey caPrivateKey = CertUtils.parsePrivateKey(request.getCaKey());

            // 3. 生成SSL密钥对（2048位）
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(2048);
            KeyPair sslKeyPair = keyGen.generateKeyPair();

            // 4. 构建SSL证书主题
            StringBuilder dnBuilder = new StringBuilder();
            dnBuilder.append("C=").append(request.getCountry());
            dnBuilder.append(",ST=").append(request.getProvince());
            dnBuilder.append(",L=").append(request.getCity());
            dnBuilder.append(",O=").append(request.getOrganization());
            dnBuilder.append(",OU=").append(request.getOrganizationalUnit());
            dnBuilder.append(",CN=").append(request.getCommonName());
            if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
                dnBuilder.append(",emailAddress=").append(request.getEmailAddress());
            }
            X500Name subject = new X500Name(dnBuilder.toString());

            // 5. 构建CA证书主题
            X500Name issuer = caCert.getSubject();

            // 6. 设置有效期（当前时间 ± expiry天）
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + request.getExpiry() * 24L * 60 * 60 * 1000);

            // 7. 创建证书构建器
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.probablePrime(128, new SecureRandom()),
                    notBefore, notAfter,
                    subject,
                    sslKeyPair.getPublic()
            );

            // 8. 添加Subject Alternative Names
            if ( request.getSubjectAltNames() != null ) {
                List<GeneralName> generalNamesList = new ArrayList<>();
                for (SubjectAltName san : request.getSubjectAltNames()) {
                    switch (san.getType()) {
                        case DNS_NAME -> generalNamesList.add(new GeneralName(GeneralName.dNSName, san.getValue()));
                        case IP_ADDRESS -> generalNamesList.add(new GeneralName(GeneralName.iPAddress, san.getValue()));
                        case URI -> generalNamesList.add(new GeneralName(GeneralName.uniformResourceIdentifier, san.getValue()));
                        case EMAIL -> generalNamesList.add(new GeneralName(GeneralName.rfc822Name, san.getValue()));
                        case DIRECTORY_NAME -> generalNamesList.add(new GeneralName(GeneralName.directoryName, san.getValue()));
                        case EDIPartyName -> generalNamesList.add(new GeneralName(GeneralName.ediPartyName, san.getValue()));
                    }
                }
                // 将列表转换为数组并构造 GeneralNames 对象
                GeneralName[] generalNamesArray = generalNamesList.toArray(new GeneralName[0]);
                GeneralNames generalNames = new GeneralNames(generalNamesArray);

                // 添加到证书扩展字段
                certBuilder.addExtension(Extension.subjectAlternativeName, false, generalNames);
            }

            // 9. 添加其他扩展字段
            certBuilder.addExtension(
                    Extension.keyUsage,
                    true,
                    new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
            );
            certBuilder.addExtension(
                    Extension.extendedKeyUsage,
                    false,
                    new ExtendedKeyUsage(
                            new KeyPurposeId[]{
                                    KeyPurposeId.id_kp_serverAuth,
                                    KeyPurposeId.id_kp_clientAuth
                            }
                    )
            );

            // 10. 创建签名器（使用CA私钥）
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .build(caPrivateKey);

            // 11. 生成最终证书
            X509CertificateHolder sslCertHolder = certBuilder.build(signer);

            // 12. 生成PEM格式的证书和私钥
            String pemCert = CertUtils.generatePemCertificate(sslCertHolder);
            String pemPrivateKey = CertUtils.generatePemPrivateKey(sslKeyPair.getPrivate());

            // 13. Base64编码
            String certBase64 = CertUtils.encodeBase64(pemCert.getBytes());
            String privKeyBase64 = CertUtils.encodeBase64(pemPrivateKey.getBytes());

            // 14. 返回响应
            return new GenResponse()
                    .setUuid(UUID.randomUUID().toString())
                    .setPrivkey(privKeyBase64)
                    .setCert(certBase64)
                    .setNotBefore(LocalDateTime.ofInstant(Instant.ofEpochMilli(notBefore.getTime()), ZoneId.systemDefault()))
                    .setNotAfter(LocalDateTime.ofInstant(Instant.ofEpochMilli(notAfter.getTime()), ZoneId.systemDefault()))
                    .setComment(request.getComment());

        } catch (Exception e) {
            throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), e.getMessage());
        }
    }

    /**
     * SSL证书续期方法（保留原密钥对）
     *
     * @param request 包含旧证书、旧私钥、CA证书/私钥和新有效期的请求
     * @return 续期后的证书和私钥（私钥与原证书一致）
     */
    public static GenResponse renewSslCertificate(CertRenewRequest request) {
        try {
            // 1. 注册 Bouncy Castle 提供者
            Security.addProvider(new BouncyCastleProvider());

            // 2. 解析证书和私钥
            X509CertificateHolder oldCertHolder = CertUtils.parseCertificate(request.getOldCert());
            PrivateKey oldPrivateKey = CertUtils.parsePrivateKey(request.getOldPrivkey());
            X509CertificateHolder caCertHolder = CertUtils.parseCertificate(request.getCa());
            PrivateKey caPrivateKey = CertUtils.parsePrivateKey(request.getCaKey());

            // 3. 提取旧证书信息
            X500Name subject = oldCertHolder.getSubject();
            X500Name issuer = caCertHolder.getSubject();
            PublicKey publicKey = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(oldCertHolder)
                    .getPublicKey();

            // 4. 设置新有效期（当前时间 ± newExpiryDays）
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + request.getNewExpiry() * 24L * 60 * 60 * 1000);

            // 5. 创建证书构建器（使用旧公钥）
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.probablePrime(128, new SecureRandom()),
                    notBefore, notAfter,
                    subject,
                    publicKey
            );

            // 6. 复制原证书的扩展字段（如 SAN、KeyUsage 等）
            Extensions oldExtensions = oldCertHolder.getExtensions();
            if (oldExtensions != null) {
                // 将 Extensions 转换为 ASN.1 序列并遍历
                ASN1Sequence extensionsSeq = (ASN1Sequence) oldExtensions.toASN1Primitive();
                for (ASN1Encodable obj : extensionsSeq) {
                    Extension extension = Extension.getInstance(obj);
                    certBuilder.addExtension(extension);
                }
            }

            // 7. 添加关键扩展字段（如 KeyUsage 和 ExtendedKeyUsage）
            // 如果旧证书未包含，则补充默认值（如服务器/客户端认证）
            if (!certBuilder.hasExtension(Extension.keyUsage)) {
                certBuilder.addExtension(
                        Extension.keyUsage,
                        true,
                        new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
                );
            }
            if (!certBuilder.hasExtension(Extension.extendedKeyUsage)) {
                certBuilder.addExtension(
                        Extension.extendedKeyUsage,
                        false,
                        new ExtendedKeyUsage(
                                new KeyPurposeId[]{
                                        KeyPurposeId.id_kp_serverAuth,
                                        KeyPurposeId.id_kp_clientAuth
                                }
                        )
                );
            }

            // 8. 使用CA私钥签名新证书
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .build(caPrivateKey);
            X509CertificateHolder newCertHolder = certBuilder.build(signer);

            // 9. 生成PEM格式的证书和私钥（私钥与原证书一致）
            String pemCert = CertUtils.generatePemCertificate(newCertHolder);
            String pemPrivateKey = CertUtils.generatePemPrivateKey(oldPrivateKey);

            // 10. Base64编码
            String certBase64 = CertUtils.encodeBase64(pemCert.getBytes());
            String privKeyBase64 = CertUtils.encodeBase64(pemPrivateKey.getBytes());

            // 11. 返回响应
            return new GenResponse()
                    .setUuid(request.getUuid())
                    .setPrivkey(privKeyBase64)
                    .setCert(certBase64)
                    .setNotBefore(LocalDateTime.ofInstant(Instant.ofEpochMilli(notBefore.getTime()), ZoneId.systemDefault()))
                    .setNotAfter(LocalDateTime.ofInstant(Instant.ofEpochMilli(notAfter.getTime()), ZoneId.systemDefault()))
                    .setComment(request.getComment());
        } catch (Exception e) {
            throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), e.getMessage());
        }
    }

}
