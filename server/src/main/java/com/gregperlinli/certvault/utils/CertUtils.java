package com.gregperlinli.certvault.utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Certificate Utils
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertUtils}
 * @date 2025/3/15 14:04
 */
public class CertUtils {

    /**
     * 辅助方法：生成 PEM 格式的证书字符串（包含头尾）
     *
     * @param certHolder 证书内容
     * @return PEM 格式的证书字符串（包含头尾）
     * @throws Exception 抛出异常
     */
    public static String generatePemCertificate(X509CertificateHolder certHolder) throws Exception {
        StringWriter writer = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(writer)) {
            pemWriter.writeObject(new PemObject("CERTIFICATE", certHolder.getEncoded()));
        }
        return writer.toString();
    }

    /**
     * 辅助方法：生成 PEM 格式的私钥字符串（包含头尾）
     *
     * @param privateKey 私钥
     * @return PEM 格式的私钥字符串（包含头尾）
     * @throws Exception 抛出异常
     */
    public static String generatePemPrivateKey(PrivateKey privateKey) throws Exception {
        StringWriter writer = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(writer)) {
            pemWriter.writeObject(new PemObject("PRIVATE KEY", PrivateKeyInfo.getInstance(privateKey.getEncoded()).getEncoded()));
        }
        return writer.toString();
    }

    /**
     * 辅助方法：Base64 编码
     *
     * @param data 需要编码的数据
     * @return Base64 编码后的字符串
     */
    public static String encodeBase64(byte[] data) {
        return java.util.Base64.getEncoder().encodeToString(data);
    }

    /**
     * 辅助方法：解析 Base64 编码的证书
     *
     * @param certBase64 Base64 编码的证书
     * @return X509CertificateHolder
     * @throws Exception 抛出异常
     */
    public static X509CertificateHolder parseCertificate(String certBase64) throws Exception {
        String pemCert = new String(java.util.Base64.getDecoder().decode(certBase64));
        try (PemReader pemReader = new PemReader(new StringReader(pemCert))) {
            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null) {
                throw new CertificateException("无效的 PEM 证书内容");
            }
            // 直接使用 DER 编码字节创建证书对象
            return new X509CertificateHolder(pemObject.getContent());
        }
    }

    /**
     * 辅助方法：解析 Base64 编码的私钥
     *
     * @param keyBase64 Base64 编码的私钥
     * @return PrivateKey
     * @throws Exception 抛出异常
     */
    public static PrivateKey parsePrivateKey(String keyBase64) throws Exception {
        String pemKey = new String(java.util.Base64.getDecoder().decode(keyBase64));
        try (PemReader pemReader = new PemReader(new StringReader(pemKey))) {
            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null) {
                throw new CertificateException("无效的 PEM 私钥内容");
            }
            // 解析私钥的 DER 编码
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemObject.getContent());
            return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        }
    }

    /**
     * 辅助方法：获取证书的 BasicConstraints
     *
     * @param cert 证书
     * @return BasicConstraints
     */
    public static BasicConstraints getBasicConstraints(X509CertificateHolder cert) {
        Extension ext = cert.getExtensions().getExtension(Extension.basicConstraints);
        if (ext != null) {
            return BasicConstraints.getInstance(ext.getParsedValue());
        }
        return null;
    }

    /**
     * 辅助方法：获取证书的密钥算法
     *
     * @param cert 证书
     * @return 密钥算法
     */
    public static String getCertificatePublicKeyAlgorithm(X509CertificateHolder cert) {
        try {
            PublicKey publicKey = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(cert)
                    .getPublicKey();
            return publicKey.getAlgorithm();
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 辅助方法：获取私钥的密钥算法
     *
     * @param privkey 私钥
     * @return 密钥算法
     */
    public static String getPrivateKeyAlgorithm(PrivateKey privkey) {
        try {
            return privkey.getAlgorithm();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 辅助方法：获取证书的密钥长度
     *
     * @param cert 证书
     * @return 密钥长度
     */
    public static Integer getCertificatePublicKeySize(X509CertificateHolder cert) {
        try {
            PublicKey publicKey = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(cert)
                    .getPublicKey();
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
            return keySize;
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 辅助方法：获取私钥的密钥长度
     *
     * @param privkey 私钥
     * @return 密钥长度
     */
    public static Integer getPrivateKeySize(PrivateKey privkey) {
        try {
            Integer keySize = null;
            if (privkey instanceof RSAPrivateKey) {
                // RSA 密钥长度（模数位数）
                keySize = ((RSAPrivateKey) privkey).getModulus().bitLength();
            } else if (privkey instanceof ECPrivateKey) {
                // EC 密钥长度（椭圆曲线位数）
                keySize = ((ECPrivateKey) privkey).getParams().getCurve().getField().getFieldSize();
            } else if ("EdDSA".equals(privkey.getAlgorithm())) {
                // Ed25519 固定密钥长度
                keySize = 256;
            } else {
                throw new UnsupportedOperationException("Unsupported key algorithm: " + privkey.getAlgorithm());
            }
            return keySize;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
