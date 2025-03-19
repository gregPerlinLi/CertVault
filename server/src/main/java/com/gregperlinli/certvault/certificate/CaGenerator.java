package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.entities.CaGenRequest;
import com.gregperlinli.certvault.domain.entities.GenResponse;
import com.gregperlinli.certvault.domain.entities.CaRenewRequest;
import com.gregperlinli.certvault.domain.exception.CertGenException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
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
import java.util.Date;
import java.util.UUID;

import static com.gregperlinli.certvault.utils.CertUtils.*;

/**
 * CA Generator
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaGenerator}
 * @date 2025/3/15 12:38
 */
public class CaGenerator {

    /**
     * 生成 CA 证书
     *
     * @param request 包含 CA 证书信息的请求
     * @return 生成的 CA 证书和私钥
     */
    public static GenResponse generateCaCertificate(CaGenRequest request) {
        try {
            // 1. 注册 Bouncy Castle 提供者
            Security.addProvider(new BouncyCastleProvider());

            // 2. 生成 RSA 密钥对（2048 位）
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(2048);
            KeyPair caKeyPair = keyGen.generateKeyPair();

            // 3. 构建 X500Name（证书主题）
            StringBuilder dnBuilder = new StringBuilder();
            dnBuilder.append("C=").append(request.getCountry());
            dnBuilder.append(",ST=").append(request.getProvince());
            dnBuilder.append(",L=").append(request.getCity());
            dnBuilder.append(",O=").append(request.getOrganization());
            dnBuilder.append(",OU=").append(request.getOrganizationalUnit());
            dnBuilder.append(",CN=").append(request.getCommonName());
            if (request.getEmailAddress() != null) {
                dnBuilder.append(",emailAddress=").append(request.getEmailAddress());
            }
            X500Name issuer = new X500Name(dnBuilder.toString());

            // 4. 设置有效期（当前时间 ± expiry 天）
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + request.getExpiry() * 24L * 60 * 60 * 1000);

            // 5. 构建证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.probablePrime(128, new SecureRandom()),
                    notBefore, notAfter,
                    issuer,
                    caKeyPair.getPublic()
            );

            // 6. 添加扩展字段
            certBuilder.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
            certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));

            // 7. 创建签名器（SHA256withRSA）
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .build(caKeyPair.getPrivate());

            // 8. 生成最终证书
            X509CertificateHolder certHolder = certBuilder.build(signer);

            // 9. 生成 PEM 格式的证书和私钥字符串（包含头尾）
            String pemCert = generatePemCertificate(certHolder);
            String pemPrivateKey = generatePemPrivateKey(caKeyPair.getPrivate());

            // 10. 对 PEM 内容进行 Base64 编码
            String certBase64 = encodeBase64(pemCert.getBytes());
            String privKeyBase64 = encodeBase64(pemPrivateKey.getBytes());

            // 11. 填充并返回响应 DTO
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
     * CA 证书续期方法
     * @param renewRequest 包含旧证书、私钥和新有效期的请求
     * @return 续期后的证书和私钥
     */
    public static GenResponse renewCaCertificate(CaRenewRequest renewRequest) {
        try {
            // 1. 注册 Bouncy Castle 提供者
            Security.addProvider(new BouncyCastleProvider());

            // 2. 解析旧证书和私钥的 Base64 内容
            X509CertificateHolder oldCert = parseCertificate(renewRequest.getOldCert());
            PrivateKey privateKey = parsePrivateKey(renewRequest.getOldPrivkey());

            // 3. 提取原始证书的主体信息
            X500Name issuer = new X500Name(String.valueOf(oldCert.getSubject()));
            PublicKey publicKey = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(oldCert)
                    .getPublicKey();

            // 4. 设置新有效期（当前时间 ± newExpiryDays）
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + renewRequest.getNewExpiry() * 24L * 60 * 60 * 1000);

            // 5. 构建新证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.probablePrime(128, new SecureRandom()),
                    notBefore, notAfter,
                    issuer,
                    publicKey
            );

            // 6. 保留原有扩展字段（如 BasicConstraints）
            certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
            certBuilder.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

            // 7. 创建签名器（使用原始私钥）
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .build(privateKey);

            // 8. 生成最终证书
            X509CertificateHolder newCertHolder = certBuilder.build(signer);

            // 9. 生成 PEM 格式的证书和私钥字符串（包含头尾）
            String pemCert = generatePemCertificate(newCertHolder);
            String pemPrivateKey = generatePemPrivateKey(privateKey);

            // 10. 对 PEM 内容进行 Base64 编码
            String newCertBase64 = encodeBase64(pemCert.getBytes());
            String newPrivKeyBase64 = encodeBase64(pemPrivateKey.getBytes());

            // 11. 填充并返回响应 DTO
            return new GenResponse()
                    .setUuid(renewRequest.getUuid())
                    .setPrivkey(newPrivKeyBase64)
                    .setCert(newCertBase64)
                    .setNotBefore(LocalDateTime.ofInstant(Instant.ofEpochMilli(notBefore.getTime()), ZoneId.systemDefault()))
                    .setNotAfter(LocalDateTime.ofInstant(Instant.ofEpochMilli(notAfter.getTime()), ZoneId.systemDefault()))
                    .setComment(renewRequest.getComment());
        } catch (Exception e) {
            throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), e.getMessage());
        }
    }

}
