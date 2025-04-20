package com.gregperlinli.certvault.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;

/**
 * Testing Certificate Utils
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code TestCertUtils}
 * @date 2025/4/20 22:04
 */
@SpringBootTest
@Slf4j
public class TestCertUtils {

    @Test
    public void testCertificateAndPrivkeyAnalyze() throws Exception {
            String privkeyBase64 = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JR1RBZ0VBTUJNR0J5cUdTTTQ5QWdFR0NDcUdTTTQ5QXdFSEJIa3dkd0lCQVFRZzlIUWlmVWZjaFV0cW9QK3EKbFVVNDNvN2ppMmJDY0xzdnE5OG1nc0hWbmx5Z0NnWUlLb1pJemowREFRZWhSQU5DQUFTS3NBWHpMSFV2VENuSApsZGE2Vms0bU9Dc2pNWC9Ec2RIZUxqL3NTVEFaTXhseXVPTHczS3RCVlJtSUlVQ3NrWVlWSldwd01pM0JFdVhyCm9kNW1WV0NWCi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K";
            String certBase64 = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUNZVENDQWdpZ0F3SUJBZ0lSQUp2SXZ5VFdTSVVtUDczYTNuUWliZGt3Q2dZSUtvWkl6ajBFQXdJd2daMHgKQ3pBSkJnTlZCQVlUQWtOT01SSXdFQVlEVlFRSURBbEhkV0Z1WjJSdmJtY3hEekFOQmdOVkJBY01Ca05oYm5SdgpiakVWTUJNR0ExVUVDZ3dNUjNKbFoxQmxjbXhwYmt4cE1SSXdFQVlEVlFRTERBbERaWEowVm1GMWJIUXhGVEFUCkJnTlZCQU1NREVObGNuUldZWFZzZENCRFFURW5NQ1VHQ1NxR1NJYjNEUUVKQVJZWVozSmxaMUJsY214cGJreHAKUUc5MWRHeHZiMnN1WTI5dE1CNFhEVEkxTURReU1ERXdNVEF3TWxvWERUTXdNRE15TlRFd01UQXdNbG93Z1o0eApDekFKQmdOVkJBWVRBa05PTVJJd0VBWURWUVFJREFsSGRXRnVaMlJ2Ym1jeER6QU5CZ05WQkFjTUJrTmhiblJ2CmJqRU5NQXNHQTFVRUNnd0VWR1Z6ZERFWE1CVUdBMVVFQ3d3T1EyVnlkRlpoZFd4MElGUmxjM1F4R1RBWEJnTlYKQkFNTUVFTmxjblJXWVhWc2RDQkpiblFnUTBFeEp6QWxCZ2txaGtpRzl3MEJDUUVXR0dkeVpXZFFaWEpzYVc1TQphVUJ2ZFhSc2IyOXJMbU52YlRCWk1CTUdCeXFHU000OUFnRUdDQ3FHU000OUF3RUhBMElBQklxd0JmTXNkUzlNCktjZVYxcnBXVGlZNEt5TXhmOE94MGQ0dVAreEpNQmt6R1hLNDR2RGNxMEZWR1lnaFFLeVJoaFVsYW5BeUxjRVMKNWV1aDNtWlZZSldqSmpBa01BNEdBMVVkRHdFQi93UUVBd0lCaGpBU0JnTlZIUk1CQWY4RUNEQUdBUUgvQWdFQgpNQW9HQ0NxR1NNNDlCQU1DQTBjQU1FUUNJSG0zYk9NWnlNdkhUcDdNYWlHd3hPNFNqVjZscGFGT3BJT1Nna3NaCkxWbVBBaUJTUmo2VmRHaWs1NWxtaE83MkdTZUM2U2hreGxlZmQ1R2Y1R3E4YlRNR3R3PT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=";

            PrivateKey privateKey = CertUtils.parsePrivateKey(privkeyBase64);
            String privateKeyAlgorithm = CertUtils.getPrivateKeyAlgorithm(privateKey);
            Integer privateKeySize = CertUtils.getPrivateKeySize(privateKey);
            log.info("Private Key Algorithm: {}", privateKeyAlgorithm);
            log.info("Private Key Size: {}", privateKeySize);

            X509CertificateHolder cert = CertUtils.parseCertificate(certBase64);
            String certificatePublicKeyAlgorithm = CertUtils.getCertificatePublicKeyAlgorithm(cert);
            Integer certificatePublicKeySize = CertUtils.getCertificatePublicKeySize(cert);
            log.info("Certificate Public Key Algorithm: {}", certificatePublicKeyAlgorithm);
            log.info("Certificate Public Key Size: {}", certificatePublicKeySize);
    }

}
