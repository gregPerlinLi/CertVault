package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.entities.CertPrivkeyResult;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.utils.CertUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.*;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.*;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Certificate Converter
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertificateConverter}
 * @date 2025/4/14 23:34
 */
public class CertConverter {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 将 Base64 编码的 PEM 证书/私钥转换为 Base64 编码的 PKCS12 (PFX)
     *
     * @param encodedCertPem Base64 编码的完整 PEM 证书（包含头尾）
     * @param encodedKeyPem  Base64 编码的完整 PEM 私钥（包含头尾）
     * @param password       密码（可为空）
     * @return Base64 编码的 PFX 字节数组
     */
    public static String convertFromPemToPfx(String encodedCertPem, String encodedKeyPem, String password) throws Exception {
        try {
            // 参数基础校验
            if ( encodedCertPem == null || encodedCertPem.isEmpty() ) {
                throw new ParamValidateException(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "At least a certificate must be provided.");
            }

            // 初始化构建器
            PKCS12PfxPduBuilder pfxBuilder = new PKCS12PfxPduBuilder();
            X509Certificate certificate = null;
            PrivateKey privateKey = null;

            // 解析证书
            certificate = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(CertUtils.parseCertificate(encodedCertPem));
            pfxBuilder.addData(new JcaPKCS12SafeBagBuilder(certificate).build());

            // 解析私钥（如果存在）
            if ( encodedKeyPem != null && !encodedKeyPem.isEmpty() ) {
                privateKey = CertUtils.parsePrivateKey(encodedKeyPem);
                pfxBuilder.addData(new JcaPKCS12SafeBagBuilder(privateKey).build());
            }

            // 创建加密器和MAC计算器
            PKCS12MacCalculatorBuilder macCalculatorBuilder = new JcePKCS12MacCalculatorBuilder()
                    .setProvider("BC")
                    .setIterationCount(1000);

            // 构建PFX
            PKCS12PfxPdu pfx = pfxBuilder.build(
                    macCalculatorBuilder,
                    ( password != null && !password.isEmpty() ) ? password.toCharArray() : new char[0]
            );

            return Base64.getEncoder().encodeToString(pfx.getEncoded());
        } catch (Exception e) {
            throw new CertificateException("PFX conversion failed: " + e.getMessage(), e);
        }
    }

    /**
     * 将 Base64 编码的 PFX 转换为独立的 PEM 证书和私钥（Base64编码后返回）
     *
     * @param pfxBase64 Base64编码的PFX数据
     * @param password  PFX密码（可为空）
     * @return 包含PEM证书和私钥的Base64编码结果对象
     */
    public static CertPrivkeyResult convertFromPfxToPem(String pfxBase64, String password) throws Exception {
        ASN1InputStream asn1In = null;
        try {
            byte[] pfxBytes = Base64.getDecoder().decode(pfxBase64);
            asn1In = new ASN1InputStream(pfxBytes);

            // 1. 解析PFX结构
            Pfx pfx = Pfx.getInstance(asn1In.readObject());
            ContentInfo authSafe = pfx.getAuthSafe();

            // 关键修正点：正确解析authSafe内容
            ASN1OctetString authSafeOctets = (ASN1OctetString) authSafe.getContent();
            ASN1InputStream authSafeStream = new ASN1InputStream(authSafeOctets.getOctets());
            ASN1Sequence authSafeSeq = ASN1Sequence.getInstance(authSafeStream.readObject());

            StringWriter certWriter = new StringWriter();
            StringWriter keyWriter = new StringWriter();

            // 2. 遍历认证安全单元
            for (ASN1Encodable contentObj : authSafeSeq) {
                ContentInfo contentInfo = ContentInfo.getInstance(contentObj);
                ASN1OctetString contentOctets = (ASN1OctetString) contentInfo.getContent();

                // 关键修正点：正确解析安全内容
                ASN1InputStream contentStream = new ASN1InputStream(contentOctets.getOctets());
                ASN1Sequence safeContents = ASN1Sequence.getInstance(contentStream.readObject());

                // 3. 解析安全内容
                for (ASN1Encodable safeBagObj : safeContents) {
                    SafeBag safeBag = SafeBag.getInstance(safeBagObj);

                    // 处理证书
                    if (safeBag.getBagId().equals(PKCSObjectIdentifiers.certBag)) {
                        CertBag certBag = CertBag.getInstance(safeBag.getBagValue());
                        ASN1OctetString certValue = ASN1OctetString.getInstance(certBag.getCertValue());
                        X509CertificateHolder certHolder = new X509CertificateHolder(certValue.getOctets());
                        try (JcaPEMWriter pemWriter = new JcaPEMWriter(certWriter)) {
                            pemWriter.writeObject(new JcaX509CertificateConverter().getCertificate(certHolder));
                        }
                    }

                    // 处理加密私钥
                    if (safeBag.getBagId().equals(PKCSObjectIdentifiers.keyBag)) {
                        // 未加密私钥
                        try {
                            PrivateKeyInfo keyInfo = PrivateKeyInfo.getInstance(safeBag.getBagValue());
                            try (JcaPEMWriter pemWriter = new JcaPEMWriter(keyWriter)) {
                                pemWriter.writeObject(new JcaPEMKeyConverter().getPrivateKey(keyInfo));
                            }
                        } catch (Exception e) {
                            throw new CertificateException("Failed to process unencrypted private key: " + e.getMessage());
                        }
                    } else if (safeBag.getBagId().equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
                        // 加密私钥
                        try {
                            EncryptedPrivateKeyInfo encKeyInfo = EncryptedPrivateKeyInfo.getInstance(safeBag.getBagValue());
                            AlgorithmIdentifier algId = encKeyInfo.getEncryptionAlgorithm();
                            byte[] encryptedData = encKeyInfo.getEncryptedData();

                            InputDecryptorProvider decryptorProvider = new JcePKCSPBEInputDecryptorProviderBuilder()
                                    .setProvider("BC")
                                    .build((password != null) ? password.toCharArray() : new char[0]);

                            InputDecryptor decryptor = decryptorProvider.get(algId);
                            try (ASN1InputStream encIn = new ASN1InputStream(decryptor.getInputStream(new ByteArrayInputStream(encryptedData)))) {
                                PrivateKeyInfo keyInfo = PrivateKeyInfo.getInstance(encIn.readObject());
                                try (JcaPEMWriter pemWriter = new JcaPEMWriter(keyWriter)) {
                                    pemWriter.writeObject(new JcaPEMKeyConverter().getPrivateKey(keyInfo));
                                }
                            }
                        } catch (Exception e) {
                            throw new CertificateException("Failed to decrypt encrypted private key: " + e.getMessage());
                        }
                    }
                }
            }

            // 4. 生成结果
            return new CertPrivkeyResult(
                    Base64.getEncoder().encodeToString(certWriter.toString().getBytes()),
                    (!keyWriter.toString().isEmpty()) ?
                            Base64.getEncoder().encodeToString(keyWriter.toString().getBytes()) :
                            null
            );
        } finally {
            if (asn1In != null) {
                asn1In.close();
            }
        }
    }

    /**
     * 将 PEM 格式的证书和私钥转换为 DER 格式的证书和私钥
     * @param encodedCertPem PEM 格式的证书
     * @param encodedKeyPem PEM 格式的私钥
     * @return DER 格式的证书和私钥
     * @throws Exception 抛出异常
     */
    public static CertPrivkeyResult convertFromPemToDer(String encodedCertPem, String encodedKeyPem) throws Exception {
        // 参数校验：证书 PEM 数据必须存在
        if (encodedCertPem == null || encodedCertPem.isEmpty()) {
            throw new ParamValidateException(
                    ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                    "PEM Certificate data cannot be empty"
            );
        }

        // 处理证书部分（必选）
        X509CertificateHolder certHolder = CertUtils.parseCertificate(encodedCertPem);
        byte[] certDerBytes = certHolder.getEncoded();
        String certBase64 = Base64.getEncoder().encodeToString(certDerBytes);

        // 处理私钥部分（可选）
        String keyBase64 = null;
        if (encodedKeyPem != null && !encodedKeyPem.isEmpty()) {
            PrivateKey privateKey = CertUtils.parsePrivateKey(encodedKeyPem);
            PrivateKeyInfo keyInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());
            byte[] keyDerBytes = keyInfo.getEncoded();
            keyBase64 = Base64.getEncoder().encodeToString(keyDerBytes);
        }

        return new CertPrivkeyResult()
                .setCertBase64(certBase64)
                .setPrivateKeyBase64(keyBase64);

    }

    public static CertPrivkeyResult convertFromDerToPem(String encodedCertDer, String encodedKeyDer) throws Exception {
        // 参数校验：证书 DER 数据必须存在
        if (encodedCertDer == null || encodedCertDer.isEmpty()) {
            throw new ParamValidateException(
                    ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(),
                    "DER Certificate data cannot be empty"
            );
        }

        // 处理证书部分（必选）
        byte[] certDerBytes = Base64.getDecoder().decode(encodedCertDer);
        X509CertificateHolder certHolder = new X509CertificateHolder(certDerBytes);
        String pemCert = CertUtils.generatePemCertificate(certHolder);
        String certBase64 = CertUtils.encodeBase64(pemCert.getBytes());

        // 处理私钥部分（可选）
        String keyBase64 = null;
        if (encodedKeyDer != null && !encodedKeyDer.isEmpty()) {
            byte[] keyDerBytes = Base64.getDecoder().decode(encodedKeyDer);
            try (StringWriter keyWriter = new StringWriter()) {
                try (PemWriter pemWriter = new PemWriter(keyWriter)) {
                    pemWriter.writeObject(new PemObject("PRIVATE KEY", keyDerBytes));
                }
                keyBase64 = CertUtils.encodeBase64(keyWriter.toString().getBytes());
            }
        }

        return new CertPrivkeyResult()
                .setCertBase64(certBase64)
                .setPrivateKeyBase64(keyBase64);
    }

}
