package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import feign.Client;
import feign.RequestInterceptor;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class FeignClientConfig {

  private final StorageProperties properties;

  @Bean
  public RequestInterceptor requestInterceptor() {
    // This interceptor injects the api key header in each request made with the client.
    return requestTemplate -> requestTemplate.header(
        "Ocp-Apim-Subscription-Key", properties.apiKey());
  }

  @Bean
  Client getFeignClient()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
        .build();

    return new Client.Default(sslContext.getSocketFactory(), NoopHostnameVerifier.INSTANCE);
  }

}
