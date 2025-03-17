package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.domain.entities.CertificateDetails;
import com.gregperlinli.certvault.utils.CertUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.Instant.ofEpochMilli;

/**
 * Certificate Decoder
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertDecoder}
 * @date 2025/3/15 16:01
 */
public class CertDecoder {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 解析证书详细信息
     *
     * @param certBase64 Base64编码的PEM证书
     * @return 证书详细信息对象
     * @throws Exception 解析异常
     */
    public static CertificateDetails decodeCertificate(String certBase64) throws Exception {
        // 1. 解析证书对象
        X509CertificateHolder certHolder = CertUtils.parseCertificate(certBase64);
        CertificateDetails details = new CertificateDetails();

        // 2. 基础信息
        details.setSubject(certHolder.getSubject().toString());
        details.setIssuer(certHolder.getIssuer().toString());
        details.setSerialNumber(certHolder.getSerialNumber().toString());
        details.setNotBefore(LocalDateTime.ofInstant(
                ofEpochMilli(certHolder.getNotBefore().getTime()),
                ZoneId.systemDefault()));
        details.setNotAfter(LocalDateTime.ofInstant(
                ofEpochMilli(certHolder.getNotAfter().getTime()),
                ZoneId.systemDefault()));

        // 3. 公钥信息
        PublicKey publicKey = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder)
                .getPublicKey();
        details.setPublicKey(publicKey);

        // 4. 扩展字段解析
        Map<String, String> extensions = new HashMap<>();
        Extensions exts = certHolder.getExtensions();
        if (exts != null) {
            ASN1Sequence extensionSequence = (ASN1Sequence) exts.toASN1Primitive();
            for (ASN1Encodable obj : extensionSequence) {
                Extension ext = Extension.getInstance(obj);
                String oid = ext.getExtnId().getId();
                String value = parseExtensionValue(oid, ext.getExtnValue().getOctets());
                extensions.put(oid, value);
            }
        }
        details.setExtensions(extensions);

        return details;
    }

    /**
     * 解析扩展字段值（示例实现）
     */
    private static String parseExtensionValue(String oid, byte[] extValueBytes) {
        try {
            if (oid.equals(Extension.subjectAlternativeName.getId())) {
                // 解析 SAN 扩展并格式化输出
                GeneralNames generalNames = GeneralNames.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                List<String> sanList = new ArrayList<>();
                for (GeneralName gn : generalNames.getNames()) {
                    switch (gn.getTagNo()) {
                        case GeneralName.dNSName:
                            sanList.add("DNS: " + gn.getName().toString());
                            break;
                        case GeneralName.iPAddress:
                            // 将字节数组转换为IP地址字符串
                            ASN1OctetString octets = (ASN1OctetString) gn.getName();
                            byte[] ipAddressBytes = octets.getOctets();
                            sanList.add("IP: " + bytesToIpAddress(ipAddressBytes));
                            break;
                        case GeneralName.uniformResourceIdentifier:
                            sanList.add("URI: " + gn.getName().toString());
                            break;
                        default:
                            sanList.add("Unknown type " + gn.getTagNo() + ": " + gn.getName().toString());
                    }
                }
                return "SAN: " + String.join(", ", sanList);
            } else if (oid.equals(Extension.keyUsage.getId())) {
                KeyUsage keyUsage = KeyUsage.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                return "KeyUsage: " + keyUsage.toString();
            } else if (oid.equals(Extension.extendedKeyUsage.getId())) {
                ASN1Primitive primitive = ASN1Primitive.fromByteArray(extValueBytes);
                if (primitive instanceof ASN1Sequence) {
                    ExtendedKeyUsage eku = ExtendedKeyUsage.getInstance(primitive);
                    return "EKU: " + eku.toString();
                } else {
                    return "EKU value type error";
                }
            } else {
                return "Unknown extension value type";
            }
        } catch (Exception e) {
            return "Parsing failed [" + oid + "]: " + e.getMessage();
        }
    }

    private static String bytesToIpAddress(byte[] bytes) {
        if (bytes.length == 4) {
            // IPv4 地址（4字节）
            return String.format("%d.%d.%d.%d",
                    bytes[0] & 0xFF,
                    bytes[1] & 0xFF,
                    bytes[2] & 0xFF,
                    bytes[3] & 0xFF);
        } else if (bytes.length == 16) {
            // IPv6 地址（16字节）
            try {
                return InetAddress.getByAddress(bytes).getHostAddress();
            } catch (UnknownHostException e) {
                return "Invalid IPv6 address: " + bytesToHexString(bytes);
            }
        } else {
            // 其他情况显示原始十六进制
            return bytesToHexString(bytes);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return "0x" + sb.toString();
    }

}
