package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import io.opentelemetry.api.OpenTelemetry;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.classic.HttpClient;
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

@Configuration
public class HttpClientConfig {

  private ApacheHttpClient5Configuration apacheHttpClient5Configuration;

  @Bean
  HttpClient getHttpClient()
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

    return apacheHttpClient5Configuration.createHttpClientBuilder().setConnectionManager(connectionManager)
        .build();
  }
}
