package com.gregperlinli.certvault.certificate;

import com.gregperlinli.certvault.domain.entities.CaGenRequest;
import com.gregperlinli.certvault.domain.entities.GenResponse;
import com.gregperlinli.certvault.domain.entities.CaRenewRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Base64;

/**
 * TestAPI
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code TestAPI}
 * @date 2025/3/15 12:55
 */
@SpringBootTest
@Slf4j
public class CaGeneratorTest {

    @Test
    public void testCaGenerate() {
        CaGenRequest request = new CaGenRequest()
                .setAlgorithm("Ed25519")
                .setKeySize(256)
                .setCountry("CN")
                .setProvince("Guangdong")
                .setCity("Canton")
                .setOrganization("GregPerlinLi")
                .setOrganizationalUnit("CertVault")
                .setCommonName("CertVault CA")
                .setEmailAddress("gregPerlinLi@outlook.com")
                .setExpiry(3650)
                .setComment("This is a Test CA.");
        GenResponse response = CaGenerator.generateCaCertificate(request);
        log.debug(response.toString());
        log.info("CA Private Key: \n {}", new String(java.util.Base64.getDecoder().decode(response.getPrivkey())));
        log.info("CA Certificate: \n {}", new String(java.util.Base64.getDecoder().decode(response.getCert())));
        log.info("CA Certificate Start Date: {}", response.getNotBefore());
        log.info("CA Certificate End Date: {}", response.getNotAfter());
        CaGenRequest intRequest = new CaGenRequest()
                .setAlgorithm("RSA")
                .setKeySize(521)
                .setParentCaPrivkey(response.getPrivkey())
                .setParentCa(response.getCert())
                .setAllowSubCa(true)
                .setCountry("CN")
                .setProvince("Guangdong")
                .setCity("Canton")
                .setOrganization("Test")
                .setOrganizationalUnit("CertVault Test")
                .setCommonName("CertVault Int CA")
                .setEmailAddress("gregPerlinLi@outlook.com")
                .setExpiry(1800)
                .setComment("This is a Test Intermediate CA.");
        response = CaGenerator.generateCaCertificate(intRequest);
        log.debug(response.toString());
        log.info("Intermediate CA Private Key: \n {}", new String(java.util.Base64.getDecoder().decode(response.getPrivkey())));
        log.info("Intermediate CA Certificate: \n {}", new String(java.util.Base64.getDecoder().decode(response.getCert())));
        log.info("Intermediate CA Certificate Start Date: {}", response.getNotBefore());
        log.info("Intermediate CA Certificate End Date: {}", response.getNotAfter());
    }

    @Test
    public void testCaRenew() {
        CaRenewRequest request = new CaRenewRequest()
                .setUuid("c031e94e-33fa-44c3-b18e-e33f6128523d")
                .setOldPrivkey("LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JR1RBZ0VBTUJNR0J5cUdTTTQ5QWdFR0NDcUdTTTQ5QXdFSEJIa3dkd0lCQVFRZzJXeCsrTE8reHNSdUh4engKTG84WFdQbDlISXNxak8rQUUyem9JYXZJVnk2Z0NnWUlLb1pJemowREFRZWhSQU5DQUFTSUlPcWhmSXVjNXVNagp4dHhqVDJLMUovYVNNUkhFS2ZjMUdMN3cyb1dkbWhFT0piTlo2SFc5ZVVBZ1dRYjBRM01QRFBmTnRqVWVFM2tZCjFHZEFOcXBYCi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K")
                .setOldCert("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUNYVENDQWdTZ0F3SUJBZ0lSQVBuTmh6bXg5QVhSTEk0OTBWRHpTQzB3Q2dZSUtvWkl6ajBFQXdJd2daMHgKQ3pBSkJnTlZCQVlUQWtOT01SSXdFQVlEVlFRSURBbEhkV0Z1WjJSdmJtY3hEekFOQmdOVkJBY01Ca05oYm5SdgpiakVWTUJNR0ExVUVDZ3dNUjNKbFoxQmxjbXhwYmt4cE1SSXdFQVlEVlFRTERBbERaWEowVm1GMWJIUXhGVEFUCkJnTlZCQU1NREVObGNuUldZWFZzZENCRFFURW5NQ1VHQ1NxR1NJYjNEUUVKQVJZWVozSmxaMUJsY214cGJreHAKUUc5MWRHeHZiMnN1WTI5dE1CNFhEVEkxTURReU1ERXdNVEF3TWxvWERUTTFNRFF4T0RFd01UQXdNbG93Z1oweApDekFKQmdOVkJBWVRBa05PTVJJd0VBWURWUVFJREFsSGRXRnVaMlJ2Ym1jeER6QU5CZ05WQkFjTUJrTmhiblJ2CmJqRVZNQk1HQTFVRUNnd01SM0psWjFCbGNteHBia3hwTVJJd0VBWURWUVFMREFsRFpYSjBWbUYxYkhReEZUQVQKQmdOVkJBTU1ERU5sY25SV1lYVnNkQ0JEUVRFbk1DVUdDU3FHU0liM0RRRUpBUllZWjNKbFoxQmxjbXhwYmt4cApRRzkxZEd4dmIyc3VZMjl0TUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFaUNEcW9YeUxuT2JqCkk4YmNZMDlpdFNmMmtqRVJ4Q24zTlJpKzhOcUZuWm9SRGlXeldlaDF2WGxBSUZrRzlFTnpEd3ozemJZMUhoTjUKR05SblFEYXFWNk1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0dHTUE4R0ExVWRFd0VCL3dRRk1BTUJBZjh3Q2dZSQpLb1pJemowRUF3SURSd0F3UkFJZ1ZDOXJvNWRqaU9SSllWemduaWkrVUNReFVmVkFZMHRmajV3WEpTbTR4bmdDCklEOUpkUmN2blZJSWU0VGp4ZW9BcTZySE1nSVJUaDZJQ3dRZjVlS0s5YmpkCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K")
                .setNewExpiry(3650)
                .setComment("Renewed CA.");
        GenResponse response = CaGenerator.renewCaCertificate(request);
        log.debug(response.toString());
        log.info("New CA Private Key: \n {}", new String(java.util.Base64.getDecoder().decode(response.getPrivkey())));
        log.info("New CA Certificate: \n {}", new String(java.util.Base64.getDecoder().decode(response.getCert())));
        log.info("New CA Certificate Start Date: {}", response.getNotBefore());
        log.info("New CA Certificate End Date: {}", response.getNotAfter());
        CaRenewRequest intRequest = new CaRenewRequest()
                .setUuid("5c266bce-ee47-4d64-85c6-295e889f8060")
                .setParentCaPrivkey("LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JR1RBZ0VBTUJNR0J5cUdTTTQ5QWdFR0NDcUdTTTQ5QXdFSEJIa3dkd0lCQVFRZzJXeCsrTE8reHNSdUh4engKTG84WFdQbDlISXNxak8rQUUyem9JYXZJVnk2Z0NnWUlLb1pJemowREFRZWhSQU5DQUFTSUlPcWhmSXVjNXVNagp4dHhqVDJLMUovYVNNUkhFS2ZjMUdMN3cyb1dkbWhFT0piTlo2SFc5ZVVBZ1dRYjBRM01QRFBmTnRqVWVFM2tZCjFHZEFOcXBYCi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K")
                .setParentCa("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUNYVENDQWdTZ0F3SUJBZ0lSQVBuTmh6bXg5QVhSTEk0OTBWRHpTQzB3Q2dZSUtvWkl6ajBFQXdJd2daMHgKQ3pBSkJnTlZCQVlUQWtOT01SSXdFQVlEVlFRSURBbEhkV0Z1WjJSdmJtY3hEekFOQmdOVkJBY01Ca05oYm5SdgpiakVWTUJNR0ExVUVDZ3dNUjNKbFoxQmxjbXhwYmt4cE1SSXdFQVlEVlFRTERBbERaWEowVm1GMWJIUXhGVEFUCkJnTlZCQU1NREVObGNuUldZWFZzZENCRFFURW5NQ1VHQ1NxR1NJYjNEUUVKQVJZWVozSmxaMUJsY214cGJreHAKUUc5MWRHeHZiMnN1WTI5dE1CNFhEVEkxTURReU1ERXdNVEF3TWxvWERUTTFNRFF4T0RFd01UQXdNbG93Z1oweApDekFKQmdOVkJBWVRBa05PTVJJd0VBWURWUVFJREFsSGRXRnVaMlJ2Ym1jeER6QU5CZ05WQkFjTUJrTmhiblJ2CmJqRVZNQk1HQTFVRUNnd01SM0psWjFCbGNteHBia3hwTVJJd0VBWURWUVFMREFsRFpYSjBWbUYxYkhReEZUQVQKQmdOVkJBTU1ERU5sY25SV1lYVnNkQ0JEUVRFbk1DVUdDU3FHU0liM0RRRUpBUllZWjNKbFoxQmxjbXhwYmt4cApRRzkxZEd4dmIyc3VZMjl0TUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFaUNEcW9YeUxuT2JqCkk4YmNZMDlpdFNmMmtqRVJ4Q24zTlJpKzhOcUZuWm9SRGlXeldlaDF2WGxBSUZrRzlFTnpEd3ozemJZMUhoTjUKR05SblFEYXFWNk1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0dHTUE4R0ExVWRFd0VCL3dRRk1BTUJBZjh3Q2dZSQpLb1pJemowRUF3SURSd0F3UkFJZ1ZDOXJvNWRqaU9SSllWemduaWkrVUNReFVmVkFZMHRmajV3WEpTbTR4bmdDCklEOUpkUmN2blZJSWU0VGp4ZW9BcTZySE1nSVJUaDZJQ3dRZjVlS0s5YmpkCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K")
                .setOldPrivkey("LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JR1RBZ0VBTUJNR0J5cUdTTTQ5QWdFR0NDcUdTTTQ5QXdFSEJIa3dkd0lCQVFRZzlIUWlmVWZjaFV0cW9QK3EKbFVVNDNvN2ppMmJDY0xzdnE5OG1nc0hWbmx5Z0NnWUlLb1pJemowREFRZWhSQU5DQUFTS3NBWHpMSFV2VENuSApsZGE2Vms0bU9Dc2pNWC9Ec2RIZUxqL3NTVEFaTXhseXVPTHczS3RCVlJtSUlVQ3NrWVlWSldwd01pM0JFdVhyCm9kNW1WV0NWCi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K")
                .setOldCert("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUNZVENDQWdpZ0F3SUJBZ0lSQUp2SXZ5VFdTSVVtUDczYTNuUWliZGt3Q2dZSUtvWkl6ajBFQXdJd2daMHgKQ3pBSkJnTlZCQVlUQWtOT01SSXdFQVlEVlFRSURBbEhkV0Z1WjJSdmJtY3hEekFOQmdOVkJBY01Ca05oYm5SdgpiakVWTUJNR0ExVUVDZ3dNUjNKbFoxQmxjbXhwYmt4cE1SSXdFQVlEVlFRTERBbERaWEowVm1GMWJIUXhGVEFUCkJnTlZCQU1NREVObGNuUldZWFZzZENCRFFURW5NQ1VHQ1NxR1NJYjNEUUVKQVJZWVozSmxaMUJsY214cGJreHAKUUc5MWRHeHZiMnN1WTI5dE1CNFhEVEkxTURReU1ERXdNVEF3TWxvWERUTXdNRE15TlRFd01UQXdNbG93Z1o0eApDekFKQmdOVkJBWVRBa05PTVJJd0VBWURWUVFJREFsSGRXRnVaMlJ2Ym1jeER6QU5CZ05WQkFjTUJrTmhiblJ2CmJqRU5NQXNHQTFVRUNnd0VWR1Z6ZERFWE1CVUdBMVVFQ3d3T1EyVnlkRlpoZFd4MElGUmxjM1F4R1RBWEJnTlYKQkFNTUVFTmxjblJXWVhWc2RDQkpiblFnUTBFeEp6QWxCZ2txaGtpRzl3MEJDUUVXR0dkeVpXZFFaWEpzYVc1TQphVUJ2ZFhSc2IyOXJMbU52YlRCWk1CTUdCeXFHU000OUFnRUdDQ3FHU000OUF3RUhBMElBQklxd0JmTXNkUzlNCktjZVYxcnBXVGlZNEt5TXhmOE94MGQ0dVAreEpNQmt6R1hLNDR2RGNxMEZWR1lnaFFLeVJoaFVsYW5BeUxjRVMKNWV1aDNtWlZZSldqSmpBa01BNEdBMVVkRHdFQi93UUVBd0lCaGpBU0JnTlZIUk1CQWY4RUNEQUdBUUgvQWdFQgpNQW9HQ0NxR1NNNDlCQU1DQTBjQU1FUUNJSG0zYk9NWnlNdkhUcDdNYWlHd3hPNFNqVjZscGFGT3BJT1Nna3NaCkxWbVBBaUJTUmo2VmRHaWs1NWxtaE83MkdTZUM2U2hreGxlZmQ1R2Y1R3E4YlRNR3R3PT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=")
                .setNewExpiry(1500)
                .setComment("Renewed Int CA.");
        response = CaGenerator.renewCaCertificate(intRequest);
        log.debug(response.toString());
        log.info("New Int CA Private Key: \n {}", new String(java.util.Base64.getDecoder().decode(response.getPrivkey())));
        log.info("New Int CA Certificate: \n {}", new String(java.util.Base64.getDecoder().decode(response.getCert())));
        log.info("New Int CA Certificate Start Date: {}", response.getNotBefore());
        log.info("New Int CA Certificate End Date: {}", response.getNotAfter());
    }
}
