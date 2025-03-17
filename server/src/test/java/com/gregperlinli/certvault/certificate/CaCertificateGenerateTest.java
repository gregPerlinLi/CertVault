package com.gregperlinli.certvault.certificate;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.*;
import java.util.Date;

/**
 * CA Generation Test
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaCertificateGenerate}
 * @date 2025/3/15 11:31
 */
@SpringBootTest
public class CaCertificateGenerateTest {

    @Test
    public void testCaGen() throws Exception {
        // 1. 注册 Bouncy Castle 提供者
        Security.addProvider(new BouncyCastleProvider());

        // 2. 生成 RSA 密钥对（2048 位）
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair caKeyPair = keyGen.generateKeyPair();

        // 3. 设置证书主题（CA 信息）
        X500Name issuer = new X500Name("CN=My CA, O=MyOrg, C=CN");

        // 4. 证书有效期（当前时间 ± 10 年）
        Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 现在
        Date notAfter = new Date(notBefore.getTime() + 10L * 365 * 24 * 60 * 60 * 1000); // 10年后

        // 5. 构建证书
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, // 签名者（CA 自签名）
                BigInteger.probablePrime(128, new SecureRandom()), // 唯一序列号
                notBefore, notAfter,
                issuer, // 证书主体（CA 自签名）
                caKeyPair.getPublic() // 直接传递 PublicKey 对象
        );

        // 6. 设置 CA 扩展字段（关键！）
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0)); // CA 标识

        // 7. 创建签名器（SHA256withRSA）
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .build(caKeyPair.getPrivate());

        // 8. 生成最终证书
        // X509CertificateHolder certHolder = certBuilder.build(signer);
        // X509Certificate caCert = new JcaX509CertificateConverter()
        //         .setProvider("BC")
        //         .getCertificate(certHolder);
        //
        // 9. 保存证书到文件（PEM 格式）
        // try (PEMWriter pemWriter = new PEMWriter(new FileWriter("ca_certificate.pem"))) {
        //     pemWriter.writeObject(certHolder);
        // }
        //
        // // 10. 导出私钥到 PEM 文件（明文）
        // PrivateKey privateKey = caKeyPair.getPrivate();
        // byte[] pkcs8Bytes = privateKey.getEncoded();
        // PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pkcs8Bytes);
        //
        // try (PemWriter keyWriter = new PemWriter(new FileWriter("ca_private_key.pem"))) {
        //     keyWriter.writeObject(new PemObject("PRIVATE KEY", privateKeyInfo.getEncoded()));
        // }
        //
        // System.out.println("CA 证书生成成功！");
        //
        // 11. （可选）加密导出私钥（如 AES-256 加密）
        // 需要用户提供密码（此处示例密码为 "password"）
        // try (PEMWriter encryptedWriter = new PEMWriter(new FileWriter("ca_encrypted_key.pem"))) {
        //     char[] password = "password".toCharArray();
        //     PemObjectGenerator gen = new JcaPKCS8Generator(
        //             caKeyPair.getPrivate(), password);
        //     encryptedWriter.writeObject(gen);
        // }

        // 8. 生成最终证书
        X509CertificateHolder certHolder = certBuilder.build(signer);

        // 9. 将证书内容打印到控制台
        StringWriter certWriter = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(certWriter)) {
            // 将 X509CertificateHolder 转换为 PemObject
            pemWriter.writeObject(new PemObject("CERTIFICATE", certHolder.getEncoded()));
        }
        System.out.println("证书内容：");
        System.out.println(certWriter);

        // 10. 将私钥内容打印到控制台
        PrivateKey privateKey = caKeyPair.getPrivate();
        byte[] pkcs8Bytes = privateKey.getEncoded();
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pkcs8Bytes);

        StringWriter keyWriter = new StringWriter();
        try (PemWriter pemKeyWriter = new PemWriter(keyWriter)) {
            pemKeyWriter.writeObject(new PemObject("PRIVATE KEY", privateKeyInfo.getEncoded()));
        }
        System.out.println("私钥内容：");
        System.out.println(keyWriter);
    }

}
