package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import feign.Client;
import feign.RequestInterceptor;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FeignClientConfig {

  private final StorageProperties properties;

//  @Bean
//  public RequestInterceptor requestInterceptor() {
//    // This interceptor injects the api key header in each request made with the client.
//    return requestTemplate -> {
//      requestTemplate.header(
//          "Ocp-Apim-Subscription-Key", properties.apiKey());
//      requestTemplate.header("x-ms-date", LocalDateTime.now().toString());
//      requestTemplate.header("x-ms-version", "2021-04-10");
//    };
//
//  }

  @Bean
  CloseableHttpClient getHttpClient()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
        .build();

    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .register("https",
            new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
        .build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
        registry);

    return HttpClients.custom().setConnectionManager(connectionManager).build();
  }

}
