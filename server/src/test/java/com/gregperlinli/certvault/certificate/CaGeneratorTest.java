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
        CaGenRequest genRequestDTO = new CaGenRequest("CN",
                "Guangdong",
                "Canton",
                "GregPerlinLi",
                "CertVault",
                "CertVault",
                "gregPerlinLi@outlook.com",
                365,
                "CertVault");
        GenResponse response = CaGenerator.generateCaCertificate(genRequestDTO);
        log.debug(response.toString());
        log.info("SSL Private Key: \n {}", new String(java.util.Base64.getDecoder().decode(response.getPrivkey())));
        log.info("CA Certificate: \n {}", new String(java.util.Base64.getDecoder().decode(response.getCert())));
        log.info("SSL Certificate Start Date: {}", response.getNotBefore());
        log.info("SSL Certificate End Date: {}", response.getNotAfter());
    }

    @Test
    public void testCaRenew() {
        CaRenewRequest renewRequestDTO = new CaRenewRequest("c031e94e-33fa-44c3-b18e-e33f6128523d",
                "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2UUlCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktjd2dnU2pBZ0VBQW9JQkFRQzNkbTFWZWQzUzQzRWgKTzVvWnBkK1lqaCtFMG9qaTR3QUJDR2pBOFBsOU90WUVkalBwcWdJNnJjR0tvdmtKb2xOdUxmTUZobGZnTEc2TgoxZXlwcE1hUlFuWC9ReUh5azNrZGpCMXdPakFXWlFsZi9BMHdnY2MxanhEbmJnK2s1OTZ2emZuYWQvUkdmYmgzCllYWGQ4ZmJldWk4VEw5UUxtdGhRS0ZZZ1dLckY0SWRuZ2xJUWwrWk9KMkd2TmxYN3p3Z2YxdUlTbkp5K0RnU2kKa0NlRWxabm9aeGloeGhHZGlWSVU1cVJ5Z05IVlptSkc1VENkTXhpWURwVEpaM2pMVHp4bHlxNnpQNTVQMUVZNQprMkRkQ3VFblBYcDVmRWVUSkM2bm5kRFprLzI0MVNneVZ1MzUwUVdxdWc3YkIxN0Q5LzI0NTZocWhqZmVaK0JWCnBLbGtaclVQQWdNQkFBRUNnZ0VBR2NaMkt1Mmh1Q2pETXFlVWlXandCcGRsSHp5VEJjbGRyVWQ0ckM3TzVvUzMKSDg1Wmtja1VZaGthV1hkM0pKN2RPTndVeWlCclJUQUk0ajE5Y0FJWjhidVVKMlpQcFN5SjloaEM1SXNRU1gwdAplWUpJZWhtcG1zRThYejlyekQvVlhmN0N6U3BRTS8waXh3YjJ6OVZZV1Vkd0tMSnVDS2xiSGJ2bENjdGJSc3g5ClpFOC9JOFVvMW9QZGpCOTdBNnQ2WlJQbXhSdWVqYUhiQzNWRHNKTTcrTkhKcXVjTm40dUZ2aDFHRUR4ZjZhMzIKdDh4N013OUx0dEM3QkUzYWFTR3ZTMjd1Q29aeWl3VXU2cFBCRHJOTHRZaHZ1a0ZmRlRkR2k3dzc4Sk9mR1c3aQpWdnVOMThHZVd0dUU4NGY1V2dHb1cyNStOaTNLNWdFZzZzZEVER0szOFFLQmdRRFl2SjdnRFc5UmtxMWNxcFJTCjFVZ1gyNTh6Wm8wYmZQbWg0cjkxSndzZVI3a1ppaDJETFphZlhrUzA0ZlhDSUE1NFd3Vkx2dVdiaVJLQWxxdGoKUUNvUXl0ODVwbzBVNmd5WWtQelVuSGF3eFpyaVRWajlJM0ZCcXVBRVpOeVRHcHp4SVhrOThHV2FnblZlU1BBKwowK1pVS1E0VkhZSFMxOVlSSDcxL2h5VTQ4UUtCZ1FEWXNxNzN3cDVUVWViQnpidjJoZ2cxY0pqeFl0dHowWE9RCjJqOW41ZjBFanRtM3VzbnlsUjMrY2pVNEw5ZzV3bkUzdkdGOFhqbnk4U09sMkc3TWIxTkJyWU9WbUVIS3ZJeFYKYmxsYzJVUHphY3FZcDFVQi9ma1VsRHdGZFV4enFxSmY5K3BVUTFKQTNuUW56ZUkzMWRtZzIySE81SWdqbFFGNApXdklxSTFyTi93S0JnRTlDMDVzK2JvcFhhTHp0eU9adUJ6cHFYOU10V3NkRnNna280am1XdUxNdktac1VibXlOCm5IclZ2Nnlpd2Z4bk9zTjdEZm5BYXpteExDb0liODhSSlJFVkVBT0dUQ3ZzMkFSbmFYN2JaWENLSTNWRk1oa0oKQUI4eXZVLytEc2hoUUNuU3d3TUt5aTF5TGpmMzFwd3paQlU5R21CUWRTekZNQXo5SFpEb3ZJMFJBb0dBWnkwTQpxZUw4UjJlam0zczlsNjlSa1VBL1VnOWp0YzF3WFBQTlpNYllwYWJ1ZXRCdy94T0k2SEd3YlVRNmk2WE5DbUNqCmVmZlRWNTBCQUNJQS8wMnlNbjlQTzViM0hIU1J6TXR4dVFlcGx1ZzBFbUw1MHF6K1A1SWswZVI3dGtiMGNRVTEKaVZGVWhKWm1iOTFUYklwaHlIa3NKTG9qSiszMmVUdTBPZVFEaERrQ2dZRUFwaVZucnBidTNZd2I3WGpBa2hKUQowZE9JejUwaGp4eUh3Mk1xcHBJWjljaHV2WTh4eWRObGFXRU9CbzAxZEFQOVFTa0VUazVWMFQ1ZWc4QlpRb1FECmpieUNXOGkvQis5VXBIME5YakpXL0c5WEZJMHhOdWJTeHhtTUlDZmk1Y1drTm9mK3VGc0FZajZ0RU5RWWdZL3kKZjgwMEhmb0JmbEpNZzcrV0dOSWsyVW89Ci0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K",
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUQ1ekNDQXMrZ0F3SUJBZ0lSQUxPV1BPQTg0TDN2dFhIR1A5N0JCODB3RFFZSktvWklodmNOQVFFTEJRQXcKZ1pveEN6QUpCZ05WQkFZVEFrTk9NUkl3RUFZRFZRUUlEQWxIZFdGdVoyUnZibWN4RHpBTkJnTlZCQWNNQmtOaApiblJ2YmpFVk1CTUdBMVVFQ2d3TVIzSmxaMUJsY214cGJreHBNUkl3RUFZRFZRUUxEQWxEWlhKMFZtRjFiSFF4CkVqQVFCZ05WQkFNTUNVTmxjblJXWVhWc2RERW5NQ1VHQ1NxR1NJYjNEUUVKQVJZWVozSmxaMUJsY214cGJreHAKUUc5MWRHeHZiMnN1WTI5dE1CNFhEVEkxTURNeE5UQTFORE16TTFvWERUSTJNRE14TlRBMU5ETXpNMW93Z1pveApDekFKQmdOVkJBWVRBa05PTVJJd0VBWURWUVFJREFsSGRXRnVaMlJ2Ym1jeER6QU5CZ05WQkFjTUJrTmhiblJ2CmJqRVZNQk1HQTFVRUNnd01SM0psWjFCbGNteHBia3hwTVJJd0VBWURWUVFMREFsRFpYSjBWbUYxYkhReEVqQVEKQmdOVkJBTU1DVU5sY25SV1lYVnNkREVuTUNVR0NTcUdTSWIzRFFFSkFSWVlaM0psWjFCbGNteHBia3hwUUc5MQpkR3h2YjJzdVkyOXRNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXQzWnRWWG5kCjB1TnhJVHVhR2FYZm1JNGZoTktJNHVNQUFRaG93UEQ1ZlRyV0JIWXo2YW9DT3EzQmlxTDVDYUpUYmkzekJZWlgKNEN4dWpkWHNxYVRHa1VKMS8wTWg4cE41SFl3ZGNEb3dGbVVKWC93Tk1JSEhOWThRNTI0UHBPZmVyODM1Mm5mMApSbjI0ZDJGMTNmSDIzcm92RXkvVUM1cllVQ2hXSUZpcXhlQ0haNEpTRUpmbVRpZGhyelpWKzg4SUg5YmlFcHljCnZnNEVvcEFuaEpXWjZHY1lvY1lSbllsU0ZPYWtjb0RSMVdaaVJ1VXduVE1ZbUE2VXlXZDR5MDg4WmNxdXN6K2UKVDlSR09aTmczUXJoSnoxNmVYeEhreVF1cDUzUTJaUDl1TlVvTWxidCtkRUZxcm9PMndkZXcvZjl1T2VvYW9ZMwozbWZnVmFTcFpHYTFEd0lEQVFBQm95WXdKREFPQmdOVkhROEJBZjhFQkFNQ0FZWXdFZ1lEVlIwVEFRSC9CQWd3CkJnRUIvd0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBQU9DQVFFQUtaU0ZpVkd1dTF0TjFFL0NveEdpWGVVSnludmIKNDNUejBrd0ZhYkczZ0M1QlhVTzdwc2g1aWVVODNkYllXck9IcE5qNW8ycFd6a0dpekU2VE5zZ2g2NFUxSUllQwpUSW9ubFBab2U5T2UvaWs1UjIxRjFTVkhQK0l6dmVGd1FnNXNWZTM2dzlWcm52cnZNcDU0NzVObDcxLzB3dE0vCit0d3hPKzFKMG5hUEd6cEVXelZyMmNMRi9taEFzREpacWZuU08wTVhscUpWSjhhTVZUTStVYWNiSWlQM2hMUXIKa0VRd3g4UW9mMTNOMVY4Ti9leWpUcHlPVzZqREVRWElnK09Iam1BeFBnZ3I1RG9DQksvM3lzZFltWGhJN3p0TwpGQy9zMi9FT1RsQmNzQS83NmhZNkcwUG1ZdnI1ZUxrekc2WklmT25sSFpmVDY5eEJlT08wNDNwdTVBPT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=",
                3650,
                "CertVault");
        GenResponse response = CaGenerator.renewCaCertificate(renewRequestDTO);
        log.debug(response.toString());
        log.info("New SSL Private Key: \n {}", new String(java.util.Base64.getDecoder().decode(response.getPrivkey())));
        log.info("New CA Certificate: \n {}", new String(java.util.Base64.getDecoder().decode(response.getCert())));
        log.info("New SSL Certificate Start Date: {}", response.getNotBefore());
        log.info("New SSL Certificate End Date: {}", response.getNotAfter());
    }
}
