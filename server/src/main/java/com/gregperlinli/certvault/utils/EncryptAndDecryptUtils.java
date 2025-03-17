package com.gregperlinli.certvault.utils;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.exception.EncryptAndDecodeException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Encrypt and Decrypt Utils
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertUtils}
 * @date 2025/3/15 14:04
 */
@Component
@Slf4j
public class EncryptAndDecryptUtils {

    @Value("${encrypt.rsa.key.public-key}")
    private String injectPublicKeyString;

    @Value("${encrypt.rsa.key.private-key}")
    private String injectPrivateKeyString;

    private static String publicKeyString;

    private static String privateKeyString;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() {
        publicKeyString = injectPublicKeyString;
        privateKeyString = injectPrivateKeyString;
    }

    /**
     * Encrypt plain text
     *
     * @param plainText to be encrypted
     * @return to be decrypted
     * @throws Exception if there is an error
     */
    public static String encrypt(String plainText) throws Exception {
        try {
            PublicKey publicKey = getPublicKeyFromBase64(publicKeyString);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] plainBytes = plainText.getBytes();
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(blockSize);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (plainBytes.length - offSet > 0) {
                if (plainBytes.length - offSet > blockSize) {
                    cache = cipher.doFinal(plainBytes, offSet, blockSize);
                } else {
                    cache = cipher.doFinal(plainBytes, offSet, plainBytes.length - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * blockSize;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new EncryptAndDecodeException(ResultStatusCodeConstant.FAILED.getResultCode(), e.getMessage());
        }
    }

    /**
     * Decrypt encrypted text
     *
     * @param encryptedText to be decrypted
     * @return to be decrypted
     * @throws Exception if there is an error
     */
    public static String decrypt(String encryptedText) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromBase64(privateKeyString);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        int blockSize = cipher.getBlockSize();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (encryptedBytes.length - offSet > 0) {
            if (encryptedBytes.length - offSet > blockSize) {
                cache = cipher.doFinal(encryptedBytes, offSet, blockSize);
            } else {
                cache = cipher.doFinal(encryptedBytes, offSet, encryptedBytes.length - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * blockSize;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    /**
     * Get public key from base64
     *
     * @param base64PublicKey public key in base64
     * @return public key
     * @throws Exception if there is an error
     */
    private static PublicKey getPublicKeyFromBase64(String base64PublicKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Get private key from base64
     *
     * @param base64PrivateKey private key in base64
     * @return private key
     * @throws Exception if there is an error
     */
    private static PrivateKey getPrivateKeyFromBase64(String base64PrivateKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        return keyFactory.generatePrivate(keySpec);
    }
}