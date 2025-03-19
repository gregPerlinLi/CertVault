package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.domain.entities.CertificateDetails;
import com.gregperlinli.certvault.utils.CertUtils;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
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
                        case GeneralName.dNSName -> sanList.add("DNS: " + gn.getName().toString());
                        case GeneralName.iPAddress -> {
                            // 将字节数组转换为IP地址字符串
                            ASN1OctetString octets = (ASN1OctetString) gn.getName();
                            byte[] ipAddressBytes = octets.getOctets();
                            sanList.add("IP: " + bytesToIpAddress(ipAddressBytes));
                        }
                        case GeneralName.uniformResourceIdentifier -> sanList.add("URI: " + gn.getName().toString());
                        case GeneralName.rfc822Name -> sanList.add("Email: " + gn.getName().toString());
                        case GeneralName.directoryName -> sanList.add("DirectoryName: " + gn.getName().toString());
                        case GeneralName.ediPartyName -> sanList.add("EDIPartyName: " + gn.getName().toString());
                        default -> sanList.add("Unknown type " + gn.getTagNo() + ": " + gn.getName().toString());
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
            }  else if (oid.equals(Extension.basicConstraints.getId())) {
                BasicConstraints bc = BasicConstraints.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                String caStatus = bc.isCA() ? "CA:TRUE" : "CA:FALSE";
                BigInteger pathLen = bc.getPathLenConstraint();
                String pathLenStr = (pathLen != null) ? ", PathLen: " + pathLen : "";
                String hexValue = bytesToHexString(extValueBytes);
                return "BasicConstraints: " + caStatus + pathLenStr + " (HEX: " + hexValue + ")";
            } else if (oid.equals(Extension.authorityKeyIdentifier.getId())) {
                AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                byte[] keyId = aki.getKeyIdentifier();
                if (keyId != null) {
                    return "AuthorityKeyIdentifier: " + bytesToHexString(keyId);
                } else {
                    return "AuthorityKeyIdentifier: No key identifier found";
                }
            } else if (oid.equals(Extension.subjectKeyIdentifier.getId())) {
                SubjectKeyIdentifier ski = SubjectKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                DEROctetString octets = (DEROctetString) ski.toASN1Primitive();
                byte[] skiBytes = octets.getOctets();
                return "SubjectKeyIdentifier: " + bytesToHexString(skiBytes);
            } else if (oid.equals(Extension.cRLDistributionPoints.getId())) {
                CRLDistPoint crlDistPoint = CRLDistPoint.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                List<String> points = new ArrayList<>();
                for (DistributionPoint dp : crlDistPoint.getDistributionPoints()) {
                    DistributionPointName dpObj = dp.getDistributionPoint();
                    if (dpObj != null && dpObj.getType() == DistributionPointName.FULL_NAME) {
                        GeneralNames names = GeneralNames.getInstance(dpObj.getName());
                        for (GeneralName gn : names.getNames()) {
                            if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                                points.add("URI: " + gn.getName().toString());
                            }
                        }
                    }
                }
                return "CRLDistributionPoints: " + String.join(", ", points);
            } else if (oid.equals(Extension.certificatePolicies.getId())) {
                CertificatePolicies policies = CertificatePolicies.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                List<String> policyList = new ArrayList<>();
                List<PolicyInformation> policyInfoList = List.of(policies.getPolicyInformation());
                for (PolicyInformation pi : policyInfoList) {
                    String policyId = pi.getPolicyIdentifier().getId();
                    if (pi.getPolicyQualifiers() != null) {
                        policyList.add(policyId + " (Qualifiers: " + pi.getPolicyQualifiers().toString() + ")");
                    } else {
                        policyList.add(policyId);
                    }
                }
                return "CertificatePolicies: " + String.join(", ", policyList);
            } else if (oid.equals(Extension.authorityInfoAccess.getId())) {
                AuthorityInformationAccess aia = AuthorityInformationAccess.getInstance(ASN1Primitive.fromByteArray(extValueBytes));
                List<String> accessDescriptions = new ArrayList<>();
                for (AccessDescription ad : aia.getAccessDescriptions()) {
                    String method = ad.getAccessMethod().getId();
                    GeneralName location = GeneralName.getInstance(ad.getAccessLocation());
                    if (location.getTagNo() == GeneralName.uniformResourceIdentifier) {
                        accessDescriptions.add(method + " -> " + location.getName().toString());
                    }
                }
                return "AuthorityInformationAccess: " + String.join(", ", accessDescriptions);
            } else {
                return "Unknown: " + bytesToHexString(extValueBytes);
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
