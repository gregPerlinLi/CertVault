package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.entities.CaGenRequest;
import com.gregperlinli.certvault.domain.entities.GenResponse;
import com.gregperlinli.certvault.domain.entities.CaRenewRequest;
import com.gregperlinli.certvault.domain.exception.CertGenException;
import com.gregperlinli.certvault.utils.CertUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static com.gregperlinli.certvault.utils.CertUtils.*;
import static org.bouncycastle.asn1.x509.ObjectDigestInfo.publicKey;

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

            // 2. 判断是否为中间 CA
            boolean isIntermediate = (request.getParentCa() != null
                    && request.getParentCaPrivkey() != null);

            // 3. 解析父 CA 的证书和私钥（仅当是中间 CA 时）
            X509CertificateHolder parentCert = null;
            PrivateKey parentPrivateKey = null;
            PublicKey parentPublicKey = null;
            if ( isIntermediate ) {
                parentCert = parseCertificate(request.getParentCa());
                parentPrivateKey = parsePrivateKey(request.getParentCaPrivkey());
                parentPublicKey = new JcaX509CertificateConverter()
                        .setProvider("BC")
                        .getCertificate(parentCert)
                        .getPublicKey();
            }

            // 4. 根据算法生成密钥对
            KeyPairGenerator keyGen = null;
            String algorithm = request.getAlgorithm();
            Integer keySize = request.getKeySize();
            if ( isIntermediate ) {
                algorithm = parentPublicKey.getAlgorithm();
                keyGen = KeyPairGenerator.getInstance(algorithm, "BC");
                if (parentPublicKey instanceof RSAPublicKey) {
                    // RSA 密钥长度（模数位数）
                    // keySize = ((RSAPublicKey) parentPublicKey).getModulus().bitLength();
                } else if (parentPublicKey instanceof ECPublicKey) {
                    // EC 密钥长度（椭圆曲线位数）
                    // keySize = ((ECPublicKey) parentPublicKey).getParams().getCurve().getField().getFieldSize();
                    // 使用ECC算法，初始化密钥长度只能为 256、384 或 521，不支持其他值
                    if ( keySize < 320 ) {
                        keySize = 256;
                    } else if ( keySize <= 452 ) {
                        keySize = 384;
                    } else {
                        keySize = 521;
                    }
                } else if ("Ed25519".equals(parentPublicKey.getAlgorithm())) {
                    // Ed25519 固定密钥长度
                    keySize = 256;
                } else {
                    throw new UnsupportedOperationException("Unsupported key algorithm: " + parentPublicKey.getAlgorithm());
                }
                keyGen.initialize(keySize, new SecureRandom());
            } else {
                keyGen = KeyPairGenerator.getInstance(algorithm, "BC");
                if ("Ed25519".equals(algorithm)) {
                    // 使用Ed25519专用参数初始化，固定密钥长度为256位
                    keySize = 256;
                    keyGen.initialize(new EdDSAParameterSpec("Ed25519"));
                } else if ("EC".equals(algorithm)) {
                    // 使用ECC算法，初始化密钥长度只能为 256、384 或 521，不支持其他值
                    if ( keySize < 320 ) {
                        keySize = 256;
                    } else if ( keySize <= 452 ) {
                        keySize = 384;
                    } else {
                        keySize = 521;
                    }
                    keyGen.initialize(keySize, new SecureRandom());
                } else {
                    keyGen.initialize(keySize, new SecureRandom());
                }
            }
            KeyPair caKeyPair = keyGen.generateKeyPair();

            // 5. 构建 X500Name（证书主题）
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
            X500Name subject = new X500Name(dnBuilder.toString());

            // 6. 设置颁发者（issuer）
            X500Name issuer = isIntermediate
                    ? new X500Name(parentCert.getSubject().toString())
                    : subject;

            // 7. 检查父 CA 的路径长度约束
            if (isIntermediate) {
                BasicConstraints parentBC = CertUtils.getBasicConstraints(parentCert);
                if (parentBC != null && parentBC.getPathLenConstraint() != null) {
                    int parentPathLen = parentBC.getPathLenConstraint().intValue();
                    int currentPathLen = request.getAllowSubCa() ? 1 : 0;
                    if (currentPathLen > parentPathLen) {
                        throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), "Parent CA does not allow issuing CA with this path length");
                    }
                }
            }

            // 8. 设置有效期（当前时间 ± expiry 天）
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + request.getExpiry() * 24L * 60 * 60 * 1000);

            // 9. 检查子 CA 有效期是否超过父 CA 的有效期
            if (isIntermediate) {
                Date parentNotAfter = parentCert.getNotAfter();
                if ( notAfter.after(parentNotAfter) ) {
                    throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), "Sub CA expiry cannot exceed parent CA expiry");
                }
            }

            // 10. 构建证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.probablePrime(128, new SecureRandom()),
                    notBefore, notAfter,
                    subject,
                    caKeyPair.getPublic()
            );

            // 11. 添加扩展字段
            // certBuilder.addExtension(Extension.keyUsage, true,
            //         new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
            // certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(1));
            certBuilder.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
            if (isIntermediate) {
                // 中间 CA：允许签发下级 CA 的条件
                // 0 表示只能签发终端，1 表示可签发 1 层下级 CA
                int pathLen = request.getAllowSubCa() ? 1 : 0;
                certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(pathLen));
            } else {
                // 根 CA：允许无限层级
                certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            }

            // 12. 签名算法选择
            String signerAlg = switch (algorithm) {
                case "RSA" -> "SHA256WithRSAEncryption";
                case "EC" -> "SHA256withECDSA";
                case "Ed25519" -> "Ed25519";
                default -> throw new IllegalArgumentException("Unsupported algorithm");
            };

            // 13. 创建签名器（SHA256withRSA）
            // ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
            //         .build(caKeyPair.getPrivate());
            ContentSigner signer = isIntermediate
                    ? new JcaContentSignerBuilder(signerAlg)
                    .build(parentPrivateKey)
                    : new JcaContentSignerBuilder(signerAlg)
                    .build(caKeyPair.getPrivate());

            // 14. 生成最终证书
            X509CertificateHolder certHolder = certBuilder.build(signer);

            // 15. 生成 PEM 格式的证书和私钥字符串（包含头尾）
            String pemCert = generatePemCertificate(certHolder);
            String pemPrivateKey = generatePemPrivateKey(caKeyPair.getPrivate());

            // 16. 对 PEM 内容进行 Base64 编码
            String certBase64 = encodeBase64(pemCert.getBytes());
            String privKeyBase64 = encodeBase64(pemPrivateKey.getBytes());

            // 17. 填充并返回响应 DTO
            return new GenResponse()
                    .setUuid(UUID.randomUUID().toString())
                    .setAlgorithm(algorithm)
                    .setKeySize(keySize)
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
     * @param request 包含旧证书、私钥和新有效期的请求
     * @return 续期后的证书和私钥
     */
    public static GenResponse renewCaCertificate(CaRenewRequest request) {
        try {
            // 1. 注册 Bouncy Castle 提供者
            Security.addProvider(new BouncyCastleProvider());

            // 2. 解析旧证书和私钥的 Base64 内容
            X509CertificateHolder oldCert = parseCertificate(request.getOldCert());
            PrivateKey privateKey = parsePrivateKey(request.getOldPrivkey());


            // 3. 判断是否为中间 CA
            boolean isIntermediate = (request.getParentCa() != null
                    && request.getParentCaPrivkey() != null);

            // 4. 解析父 CA 的证书和私钥（仅当是中间 CA 时）
            X509CertificateHolder parentCert = null;
            PrivateKey parentPrivateKey = null;
            if (isIntermediate) {
                parentCert = parseCertificate(request.getParentCa());
                parentPrivateKey = parsePrivateKey(request.getParentCaPrivkey());
            }

            // 5. 验证父 CA 有效性
            if (isIntermediate) {
                if (!parentCert.getSubject().equals(oldCert.getIssuer())) {
                    throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), "Parent CA does not match the issuer of the old certificate");
                }
            }

            // 6. 检查父 CA 的路径长度约束
            if (isIntermediate) {
                BasicConstraints parentBC = CertUtils.getBasicConstraints(parentCert);
                if (parentBC != null && parentBC.getPathLenConstraint() != null) {
                    int parentPathLen = parentBC.getPathLenConstraint().intValue();
                    int currentPathLen = request.getAllowSubCa() ? 1 : 0;
                    if (currentPathLen > parentPathLen) {
                        throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), "Parent CA does not allow issuing CA with this path length");
                    }
                }
            }

            // 7. 提取原始证书的主体信息
            X500Name issuer = new X500Name(String.valueOf(oldCert.getIssuer()));
            X500Name subject = new X500Name(String.valueOf(oldCert.getSubject()));
            PublicKey publicKey = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(oldCert)
                    .getPublicKey();

            // 8. 设置新有效期（当前时间 ± newExpiryDays）
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + request.getNewExpiry() * 24L * 60 * 60 * 1000);

            // 9. 检查子 CA 有效期是否超过父 CA 的有效期
            if (isIntermediate) {
                Date parentNotAfter = parentCert.getNotAfter();
                if ( notAfter.after(parentNotAfter) ) {
                    throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), "Sub CA expiry cannot exceed parent CA expiry");
                }
            }

            // 10. 构建新证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.probablePrime(128, new SecureRandom()),
                    notBefore, notAfter,
                    subject,
                    publicKey
            );

            // 11. 保留原有扩展字段（如 BasicConstraints）
            // certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
            // certBuilder.addExtension(Extension.keyUsage, true,
            //        new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
            certBuilder.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
            if (isIntermediate) {
                int pathLen = request.getAllowSubCa() ? 1 : 0;
                certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(pathLen));
            } else {
                certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            }

            // 12. 签名算法选择
            String algorithm = publicKey.getAlgorithm();
            String signerAlg = switch (algorithm) {
                case "RSA" -> "SHA256WithRSAEncryption";
                case "EC" -> "SHA256withECDSA";
                case "Ed25519" -> "Ed25519";
                default -> throw new IllegalArgumentException("Unsupported algorithm");
            };

            // 13. 创建签名器（使用原始私钥）
            // ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
            //         .build(privateKey);
            ContentSigner signer = isIntermediate
                    ? new JcaContentSignerBuilder(signerAlg).build(parentPrivateKey)
                    : new JcaContentSignerBuilder(signerAlg).build(privateKey);

            // 14. 生成最终证书
            X509CertificateHolder newCertHolder = certBuilder.build(signer);

            // 15. 生成 PEM 格式的证书和私钥字符串（包含头尾）
            String pemCert = generatePemCertificate(newCertHolder);
            String pemPrivateKey = generatePemPrivateKey(privateKey);

            // 16. 对 PEM 内容进行 Base64 编码
            String newCertBase64 = encodeBase64(pemCert.getBytes());
            String newPrivKeyBase64 = encodeBase64(pemPrivateKey.getBytes());

            // 17. 获取密钥长度
            Integer keySize = null;
            if (publicKey instanceof RSAPublicKey) {
                // RSA 密钥长度（模数位数）
                keySize = ((RSAPublicKey) publicKey).getModulus().bitLength();
            } else if (publicKey instanceof ECPublicKey) {
                // EC 密钥长度（椭圆曲线位数）
                keySize = ((ECPublicKey) publicKey).getParams().getCurve().getField().getFieldSize();
            } else if ("Ed25519".equals(publicKey.getAlgorithm())) {
                // Ed25519 固定密钥长度
                keySize = 256;
            } else {
                throw new UnsupportedOperationException("Unsupported key algorithm: " + publicKey.getAlgorithm());
            }

            // 18. 填充并返回响应 DTO
            return new GenResponse()
                    .setUuid(request.getUuid())
                    .setAlgorithm(algorithm)
                    .setKeySize(keySize)
                    .setPrivkey(newPrivKeyBase64)
                    .setCert(newCertBase64)
                    .setNotBefore(LocalDateTime.ofInstant(Instant.ofEpochMilli(notBefore.getTime()), ZoneId.systemDefault()))
                    .setNotAfter(LocalDateTime.ofInstant(Instant.ofEpochMilli(notAfter.getTime()), ZoneId.systemDefault()))
                    .setComment(request.getComment());
        } catch (Exception e) {
            throw new CertGenException(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode(), e.getMessage());
        }
    }

}
